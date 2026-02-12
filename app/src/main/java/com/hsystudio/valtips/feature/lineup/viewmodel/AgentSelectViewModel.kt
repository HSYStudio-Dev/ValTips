package com.hsystudio.valtips.feature.lineup.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.ad.AdConfig
import com.hsystudio.valtips.data.ad.AdRepository
import com.hsystudio.valtips.data.local.AppPrefsManager
import com.hsystudio.valtips.data.local.dao.TierDao
import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.domain.model.RoleFilterItem
import com.hsystudio.valtips.domain.repository.AgentRepository
import com.hsystudio.valtips.domain.repository.LineupRepository
import com.hsystudio.valtips.domain.repository.MapRepository
import com.hsystudio.valtips.feature.lineup.model.AgentSelectUiState
import com.hsystudio.valtips.feature.lineup.model.LineupStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// 왼쪽 코어 상태(역할/선택 역할/요원 리스트)
private data class AgentSelectCoreLeft(
    val roles: List<RoleFilterItem>,
    val selectedRoleUuid: String?,
    val agents: List<AgentCardItem>
)

// 오른쪽 코어 상태(라인업 상태/맵 스플래시/플레이스홀더 아이콘)
private data class AgentSelectCoreRight(
    val lineupStatus: List<LineupStatus>,
    val mapSplashLocal: String?,
    val placeholderIconLocal: String?
)

@HiltViewModel
class AgentSelectViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val agentRepository: AgentRepository,
    private val lineupRepository: LineupRepository,
    private val tierDao: TierDao,
    private val appPrefsManager: AppPrefsManager,
    private val adRepository: AdRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // NavGraph에서 전달된 맵 UUID
    val mapUuid: String = checkNotNull(savedStateHandle["mapUuid"])

    // 역할 필터 상태
    private val rolesState = MutableStateFlow<List<RoleFilterItem>>(emptyList())
    private val selectedRoleUuidState = MutableStateFlow<String?>(null)

    // 라인업 상태 / 로딩 / 에러
    private val lineupStatusFlow = MutableStateFlow<List<LineupStatus>>(emptyList())
    private val isLoadingState = MutableStateFlow(true)
    private val errorState = MutableStateFlow<String?>(null)

    // 역할 선택이 바뀔 때마다 요원 목록을 실시간 구독
    @OptIn(ExperimentalCoroutinesApi::class)
    private val agentsState =
        selectedRoleUuidState.flatMapLatest { roleUuid: String? ->
            agentRepository.observeAgents(roleUuid)
        }

    // 선택된 맵 / 플레이스홀더 아이콘
    private val mapSplashFlow = mapRepository.observeMapSplashLocal(mapUuid)
    private val placeholderIconFlow = tierDao.observeTierZeroIconLocal()

    // 광고 상태 관리
    private val _nativeAdState = MutableStateFlow<NativeAdUiState>(NativeAdUiState.Loading)
    val nativeAdState: StateFlow<NativeAdUiState> = _nativeAdState.asStateFlow()

    // 역할 + 선택 역할 + 요원 리스트를 합친 Flow
    private val leftCoreFlow = combine(
        rolesState,
        selectedRoleUuidState,
        agentsState
    ) { roles, selectedRoleUuid, agents ->
        AgentSelectCoreLeft(
            roles = roles,
            selectedRoleUuid = selectedRoleUuid,
            agents = agents
        )
    }

    // 라인업 상태 + 맵 스플래시 + 플레이스홀더 아이콘을 합친 Flow
    private val rightCoreFlow = combine(
        lineupStatusFlow,
        mapSplashFlow,
        placeholderIconFlow
    ) { lineupStatus, mapSplashLocal, placeholderIconLocal ->
        AgentSelectCoreRight(
            lineupStatus = lineupStatus,
            mapSplashLocal = mapSplashLocal,
            placeholderIconLocal = placeholderIconLocal
        )
    }

    // 왼쪽 + 오른쪽 코어 상태를 하나의 Flow로 합친 변수
    private val coreStateFlow = combine(
        leftCoreFlow,
        rightCoreFlow
    ) { left, right ->
        val lineupMap = right.lineupStatus.associate { status ->
            status.uuid to status.hasLineups
        }

        AgentSelectUiState(
            roles = left.roles,
            selectedRoleUuid = left.selectedRoleUuid,
            agents = left.agents,
            lineupStatus = lineupMap,
            mapSplashLocal = right.mapSplashLocal,
            placeholderIconLocal = right.placeholderIconLocal
        )
    }

    // 로딩&에러 상태를 합친 최종 UI 상태
    val uiStateFlow = combine(
        coreStateFlow,
        isLoadingState,
        errorState,
        appPrefsManager.isProMemberFlow
    ) { core, isLoading, error, isPro ->
        core.copy(
            isLoading = isLoading,
            error = error,
            isProMember = isPro
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AgentSelectUiState()
    )

    // 진입 시 1회 불러오기
    init {
        viewModelScope.launch {
            rolesState.value = agentRepository.getRoleFilters()
        }
        getAgentLineupStatus()
        observeMembership()
    }

    // 맵 기준 요원별 라인업 상태 불러오기
    fun getAgentLineupStatus() {
        viewModelScope.launch {
            isLoadingState.value = true
            errorState.value = null

            lineupRepository
                .getAgentsLineupStatus(mapUuid)
                .onSuccess { list ->
                    lineupStatusFlow.value = list
                }
                .onFailure { e ->
                    Log.e("AgentSelectViewModel", "맵 기준 라인업 조회 실패 : ${e.message}")
                    errorState.value = "라인업 정보를 불러오지 못했습니다."
                }
            isLoadingState.value = false
        }
    }

    // 역할 선택
    fun selectRole(uuidOrEmpty: String) {
        selectedRoleUuidState.value = uuidOrEmpty.ifEmpty { null }
    }

    // AppPrefsManager의 멤버십 상태 구독
    private fun observeMembership() {
        viewModelScope.launch {
            appPrefsManager.isProMemberFlow.collectLatest { isPro ->
                if (isPro) {
                    destroyAd()
                } else {
                    loadAd()
                }
            }
        }
    }

    // 광고 로드 함수
    private fun loadAd() {
        if (_nativeAdState.value is NativeAdUiState.Success) return

        _nativeAdState.value = NativeAdUiState.Loading

        adRepository.loadNativeAd(
            adUnitId = AdConfig.AgentSelectNativeId,
            onLoaded = { ad ->
                destroyAd()
                _nativeAdState.value = NativeAdUiState.Success(ad)
            },
            onFailed = {
                _nativeAdState.value = NativeAdUiState.Error
            }
        )
    }

    // 광고 메모리 해제 함수
    private fun destroyAd() {
        val currentState = _nativeAdState.value
        if (currentState is NativeAdUiState.Success) {
            currentState.ad.destroy()
        }
        _nativeAdState.value = NativeAdUiState.Loading
    }

    // ViewModel이 사라질 때 광고 객체 정리
    override fun onCleared() {
        super.onCleared()
        destroyAd()
    }
}
