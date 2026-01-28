package com.hsystudio.valtips.feature.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.ad.AdConfig
import com.hsystudio.valtips.data.ad.AdRepository
import com.hsystudio.valtips.data.local.AppPrefsManager
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.domain.repository.AgentRepository
import com.hsystudio.valtips.feature.agent.model.AgentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgentsViewModel @Inject constructor(
    private val repo: AgentRepository,
    private val appPrefsManager: AppPrefsManager,
    private val adRepository: AdRepository
) : ViewModel() {
    // 선택된 역할
    private val selectedRoleUuid = MutableStateFlow<String?>(null)

    // 역할 데이터
    private val rolesFlow = flow { emit(repo.getRoleFilters()) }

    // 요원 데이터
    @OptIn(ExperimentalCoroutinesApi::class)
    private val agentsFlow = selectedRoleUuid.flatMapLatest { roleUuid ->
        repo.observeAgents(roleUuid)
    }

    // 광고 상태 관리
    private val _nativeAdState = MutableStateFlow<NativeAdUiState>(NativeAdUiState.Loading)
    val nativeAdState: StateFlow<NativeAdUiState> = _nativeAdState.asStateFlow()

    // 프로 멤버십 상태
    private val isProMember = MutableStateFlow(false)

    // 최종 UI 상태
    val uiState: StateFlow<AgentsUiState> = combine(
        selectedRoleUuid,
        rolesFlow,
        agentsFlow,
        isProMember
    ) { selectedRole, roles, agents, isPro ->

        // 역할 데이터 검사
        val hasCorruptedRole = roles.any { role ->
            role.uuid.isNotEmpty() && role.roleIconLocal.isNullOrBlank()
        }

        // 요원 데이터 검사
        val hasCorruptedAgent = agents.any { agent ->
            agent.agentIconLocal.isNullOrBlank()
        }

        // 데이터가 아예 없는 경우
        val isDataEmpty = roles.isEmpty() || (selectedRole == null && agents.isEmpty())

        if (hasCorruptedRole || hasCorruptedAgent || isDataEmpty) {
            AgentsUiState(
                isLoading = false,
                error = "일부 리소스를 불러오지 못했습니다.\n문제가 계속 된다면 설정 탭에서\n최신 리소스 다운로드를 진행해 주세요."
            )
        } else {
            AgentsUiState(
                isLoading = false,
                roles = roles,
                agents = agents,
                selectedRoleUuid = selectedRole,
                error = null,
                isProMember = isPro
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AgentsUiState(isLoading = true)
    )

    init {
        observeMembership()
    }

    // AppPrefsManager의 멤버십 상태 구독
    private fun observeMembership() {
        viewModelScope.launch {
            appPrefsManager.isProMemberFlow.collectLatest { isPro ->
                isProMember.value = isPro
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
            adUnitId = AdConfig.AgentsNativeId,
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

    // 역할 선택
    fun selectRole(uuidOrEmpty: String) {
        selectedRoleUuid.value = uuidOrEmpty.ifEmpty { null }
    }
}
