package com.hsystudio.valtips.feature.lineup.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.ad.AdConfig
import com.hsystudio.valtips.data.ad.AdRepository
import com.hsystudio.valtips.data.local.AppPrefsManager
import com.hsystudio.valtips.data.local.dao.TierDao
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.domain.repository.AgentRepository
import com.hsystudio.valtips.domain.repository.LineupRepository
import com.hsystudio.valtips.domain.repository.MapRepository
import com.hsystudio.valtips.feature.lineup.model.LineupStatus
import com.hsystudio.valtips.feature.lineup.model.MapSelectUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapSelectViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val agentRepository: AgentRepository,
    private val lineupRepository: LineupRepository,
    private val tierDao: TierDao,
    private val appPrefsManager: AppPrefsManager,
    private val adRepository: AdRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // NavGraph에서 전달된 요원 UUID
    val agentUuid: String = checkNotNull(savedStateHandle["agentUuid"])

    // 라인업 상태 / 로딩 / 에러
    private val lineupStatusFlow = MutableStateFlow<List<LineupStatus>>(emptyList())
    private val isLoadingState = MutableStateFlow(true)
    private val errorState = MutableStateFlow<String?>(null)

    // 선택 요원 / 플레이스홀더 아이콘
    private val selectedAgentFlow = agentRepository.observeAgentIconLocal(agentUuid)
    private val placeholderIconFlow = tierDao.observeTierZeroIconLocal()

    // 광고 상태 관리
    private val _nativeAdState = MutableStateFlow<NativeAdUiState>(NativeAdUiState.Loading)
    val nativeAdState: StateFlow<NativeAdUiState> = _nativeAdState.asStateFlow()

    // 핵심 데이터를 하나의 Flow로 합친 변수
    private val coreStateFlow = combine(
        mapRepository.observeCurrentActName(),
        mapRepository.observeMapCards(),
        lineupStatusFlow,
        selectedAgentFlow,
        placeholderIconFlow
    ) { actName, maps, lineupStatus, agentIconLocal, placeholderIconLocal ->
        val (active, retired) = maps.partition { it.isActiveInRotation }

        MapSelectUiState(
            actTitle = actName?.let { "$it 맵" },
            activeMaps = active,
            retiredMaps = retired,
            lineupStatus = lineupStatus.associate { it.uuid to it.hasLineups },
            agentIconLocal = agentIconLocal,
            placeholderIconLocal = placeholderIconLocal
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
        initialValue = MapSelectUiState()
    )

    // 진입 시 1회 불러오기
    init {
        getMapLineupStatus()
        observeMembership()
    }

    // 요원 기준 맵별 라인업 상태 불러오기
    fun getMapLineupStatus() {
        viewModelScope.launch {
            isLoadingState.value = true
            errorState.value = null

            lineupRepository.getMapsLineupStatus(agentUuid)
                .onSuccess { list ->
                    lineupStatusFlow.value = list
                }
                .onFailure { e ->
                    Log.e("MapSelectViewModel", "요원 기준 라인업 조회 실패 : ${e.message}")
                    errorState.value = "라인업 정보를 불러오지 못했습니다."
                }
            isLoadingState.value = false
        }
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
            adUnitId = AdConfig.MapSelectNativeId,
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
