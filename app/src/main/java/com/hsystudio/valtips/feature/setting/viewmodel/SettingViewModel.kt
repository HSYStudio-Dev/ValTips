package com.hsystudio.valtips.feature.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.auth.AuthTokenBus
import com.hsystudio.valtips.data.auth.FakeRiotAccountRepository
import com.hsystudio.valtips.data.local.AppPrefsManager
import com.hsystudio.valtips.feature.setting.SettingDialogState
import com.hsystudio.valtips.feature.setting.SettingUiEffect
import com.hsystudio.valtips.feature.setting.SettingUiEvent
import com.hsystudio.valtips.feature.setting.model.SettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val accountRepository: FakeRiotAccountRepository,
    private val authTokenBus: AuthTokenBus,
    private val appPrefsManager: AppPrefsManager
) : ViewModel() {
    // UI 상태
    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    // 1회성으로 처리해야 하는 이벤트
    private val _effect = Channel<SettingUiEffect>(Channel.BUFFERED)
    val effect: Flow<SettingUiEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            accountRepository.restoreFromStore()
        }
        observeAccounts()
        observeAuthTokens()
    }

    // 레포 계정 목록이 바뀔 때마다 UIState 갱신
    private fun observeAccounts() {
        viewModelScope.launch {
            accountRepository.accounts.collect { list ->
                val active = list.firstOrNull { it.isActive }

                _uiState.update {
                    it.copy(
                        accounts = list,
                        currentAccount = active,
                        isLoggedIn = list.isNotEmpty()
                    )
                }
            }
        }
    }

    // 토큰 이벤트가 들어오면 최신 토큰 기준으로 계정 추가
    private fun observeAuthTokens() {
        viewModelScope.launch {
            authTokenBus.tokens.collectLatest { token ->
                handleLoginToken(token)
            }
        }
    }

    // 사용자 액션을 한 곳에서 처리하는 이벤트 핸들러
    fun onEvent(event: SettingUiEvent) {
        when (event) {
            // Todo : [임시] "로그인" 버튼 클릭 → 임시 로그인 다이얼로그 오픈
            SettingUiEvent.ClickRiotLogin -> {
                _uiState.update {
                    it.copy(dialogState = SettingDialogState.DevLoginPrompt(isAddAccount = false))
                }
            }

            // Todo : [임시] "계정 추가" 버튼 클릭 → 프로 멤버십&최대 3개 조건 검사 후 임시 로그인 다이얼로그 오픈
            SettingUiEvent.ClickAddAccount -> {
                val state = _uiState.value

                if (!state.isProMember) {
                    sendEffect(SettingUiEffect.ShowMessage("프로 멤버십 전용 기능입니다."))
                    return
                }

                if (state.accounts.size >= 3) {
                    sendEffect(SettingUiEffect.ShowMessage("계정은 최대 3개까지 추가할 수 있습니다."))
                    return
                }

                _uiState.update {
                    it.copy(dialogState = SettingDialogState.DevLoginPrompt(isAddAccount = true))
                }
            }

            // 딥링크로 토큰이 들어오면 즉시 계정 추가
            is SettingUiEvent.OnLoginDeepLink -> handleLoginToken(event.token)

            // 비활성 계정 카드 클릭 → 계정 전환 다이얼로그 오픈
            is SettingUiEvent.ClickAccountCard -> onAccountCardClicked(event.accountId)

            // 계정 카드의 로그아웃 클릭 → 로그아웃 다이얼로그 오픈
            is SettingUiEvent.ClickAccountLogout -> showLogoutDialog(event.accountId)

            // 다이얼로그 확인 버튼 클릭 → 현재 다이얼로그 타입에 맞는 동작 실행
            SettingUiEvent.ConfirmDialog -> onConfirmDialog()

            // 다이얼로그 취소/닫기 버튼 클릭 → 다이얼로그 닫기
            SettingUiEvent.DismissDialog -> dismissDialog()

            // 멤버십 관리 버튼 클릭 → 라우터에서 화면 이동 처리하도록 Effect 발행
            SettingUiEvent.ClickMembershipManage ->
                sendEffect(SettingUiEffect.NavigateToMembership)

            // Todo : [DEV 옵션] 프로 멤버십 토글
            is SettingUiEvent.ToggleProMembership -> {
                _uiState.update {
                    it.copy(isProMember = event.enabled)
                }

                sendEffect(
                    SettingUiEffect.ShowMessage(
                        if (event.enabled) {
                            "프로 멤버십이 활성화되었습니다."
                        } else {
                            "프로 멤버십이 비활성화되었습니다."
                        }
                    )
                )
            }

            // Todo : [DEV 옵션] 온보딩 기록 삭제 버튼
            is SettingUiEvent.OnClickDeleteOnboarding -> {
                viewModelScope.launch {
                    appPrefsManager.clearOnboarding()
                }
            }

            // Todo : [DEV 옵션] 약관 동의 기록 삭제 버튼
            is SettingUiEvent.OnClickDeleteAgree -> {
                viewModelScope.launch {
                    appPrefsManager.clearAcceptedPolicyVersions()
                }
            }

            else -> Unit
        }
    }

    // ─────────────────────────────
    // Login / Add Account
    // ─────────────────────────────
    // 토큰을 받아 계정을 추가
    private fun handleLoginToken(token: String) {
        viewModelScope.launch {
            accountRepository.addAccount(token)
        }
    }

    // ─────────────────────────────
    // Account Switch / Logout
    // ─────────────────────────────
    // 계정 전환
    private fun onAccountCardClicked(accountId: String) {
        val state = _uiState.value
        val target = state.accounts.firstOrNull { it.accountId == accountId } ?: return
        val currentId = state.currentAccount?.accountId
        if (currentId == target.accountId) return

        _uiState.update {
            it.copy(
                dialogState = SettingDialogState.ConfirmSwitch(
                    targetAccount = target
                )
            )
        }
    }

    // 특정 계정 로그아웃
    private fun showLogoutDialog(accountId: String) {
        val target = _uiState.value.accounts.firstOrNull {
            it.accountId == accountId
        } ?: return

        _uiState.update {
            it.copy(
                dialogState = SettingDialogState.ConfirmLogout(
                    targetAccount = target
                )
            )
        }
    }

    private fun onConfirmDialog() {
        // 현재 떠 있는 다이얼로그 타입을 기준으로 실행할 액션을 결정
        val dialog = _uiState.value.dialogState ?: return

        viewModelScope.launch {
            when (dialog) {
                // 계정 전환 확정 → 레포에서 활성 계정 변경
                is SettingDialogState.ConfirmSwitch -> {
                    accountRepository.switchAccount(dialog.targetAccount.accountId)
                    sendEffect(SettingUiEffect.ShowMessage("계정이 전환되었습니다."))
                }

                // 로그아웃 확정 → 레포에서 계정 제거(활성 계정이면 자동 전환 로직 포함)
                is SettingDialogState.ConfirmLogout -> {
                    accountRepository.removeAccount(dialog.targetAccount.accountId)
                    sendEffect(SettingUiEffect.ShowMessage("계정이 로그아웃되었습니다."))
                }

                // Todo : [임시] 임시 로그인 확정 → 랜덤 토큰으로 Fake 계정 추가
                is SettingDialogState.DevLoginPrompt -> {
                    val fakeToken = UUID.randomUUID().toString()
                    handleLoginToken(fakeToken)
                    sendEffect(SettingUiEffect.ShowMessage("임시 로그인으로 처리되었습니다."))
                }
            }
        }
        // 액션 처리 후 다이얼로그 닫기
        dismissDialog()
    }

    // 다이얼로그 상태 초기화
    private fun dismissDialog() {
        _uiState.update { it.copy(dialogState = null) }
    }

    // ─────────────────────────────
    // Effect helper
    // ─────────────────────────────
    // 1회성 이벤트를 화면으로 전달하는 공용 함수
    private fun sendEffect(effect: SettingUiEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
