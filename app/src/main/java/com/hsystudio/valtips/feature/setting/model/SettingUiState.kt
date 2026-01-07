package com.hsystudio.valtips.feature.setting.model

import com.hsystudio.valtips.data.auth.FakeRiotAccount
import com.hsystudio.valtips.feature.setting.SettingDialogState

// 설정 화면 UI 상태
data class SettingUiState(
    val isLoggedIn: Boolean = false,
    val isProMember: Boolean = false,

    val currentAccount: FakeRiotAccount? = null,
    val accounts: List<FakeRiotAccount> = emptyList(),

    val membershipNextBillingDate: String? = null,

    val dialogState: SettingDialogState? = null,
    val isLoading: Boolean = false
)
