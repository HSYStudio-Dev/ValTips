package com.hsystudio.valtips.feature.setting.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.BuildConfig
import com.hsystudio.valtips.feature.setting.SettingDialogState
import com.hsystudio.valtips.feature.setting.SettingUiEvent
import com.hsystudio.valtips.feature.setting.model.SettingUiState
import com.hsystudio.valtips.feature.setting.ui.component.AddAccountCard
import com.hsystudio.valtips.feature.setting.ui.component.AppInfoRow
import com.hsystudio.valtips.feature.setting.ui.dialog.ConfirmDialog
import com.hsystudio.valtips.feature.setting.ui.component.CurrentAccountCard
import com.hsystudio.valtips.feature.setting.ui.component.EmptyRiotAccountCard
import com.hsystudio.valtips.feature.setting.ui.component.MembershipCard
import com.hsystudio.valtips.feature.setting.ui.component.RiotAccountCard
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun SettingScreen(
    uiState: SettingUiState,
    onEvent: (SettingUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Setting")
        }
    ) { values ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = values.calculateTopPadding())
        ) {
            val horizontalPadding = when {
                maxWidth < 400.dp -> 24.dp
                maxWidth < 600.dp -> 32.dp
                else -> 40.dp
            }

            val verticalPadding = when {
                maxHeight < 600.dp -> 24.dp
                maxHeight < 800.dp -> 32.dp
                else -> 40.dp
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    vertical = verticalPadding,
                    horizontal = horizontalPadding
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 현재 계정
                item {
                    Column {
                        SectionTitle("현재 계정")

                        Spacer(Modifier.height(6.dp))

                        CurrentAccountCard(
                            isLoggedIn = uiState.isLoggedIn,
                            currentAccount = uiState.currentAccount,
                            onLoginClick = { onEvent(SettingUiEvent.ClickRiotLogin) }
                        )
                    }
                }

                // 연결된 라이엇 계정
                item {
                    Column {
                        SectionTitle("라이엇 계정")

                        Spacer(Modifier.height(6.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!uiState.isLoggedIn) {
                                // 로그인 X
                                EmptyRiotAccountCard()
                            } else {
                                // 로그인 O
                                uiState.accounts.forEach { account ->
                                    RiotAccountCard(
                                        account = account,
                                        onClick = {
                                            onEvent(SettingUiEvent.ClickAccountCard(account.accountId))
                                        },
                                        onLogout = {
                                            onEvent(SettingUiEvent.ClickAccountLogout(account.accountId))
                                        }
                                    )
                                }

                                AddAccountCard(
                                    enabled = uiState.isProMember,
                                    onAdd = { onEvent(SettingUiEvent.ClickAddAccount) }
                                )
                            }
                        }
                    }
                }

                // 멤버십
                item {
                    Column {
                        SectionTitle("프로 멤버십")

                        Spacer(Modifier.height(6.dp))

                        MembershipCard(
                            isProMember = uiState.isProMember,
                            nextBillingDate = uiState.membershipNextBillingDate,
                            onManageClick = { onEvent(SettingUiEvent.ClickMembershipManage) }
                        )
                    }
                }

                // 앱 정보
                item {
                    Column {
                        SectionTitle("앱 정보")

                        Spacer(Modifier.height(6.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppInfoRow(
                                title = "이용약관",
                                onClick = { onEvent(SettingUiEvent.ClickTerms) }
                            )
                            AppInfoRow(
                                title = "개인정보처리방침",
                                onClick = { onEvent(SettingUiEvent.ClickPrivacy) }
                            )
                            AppInfoRow(
                                title = "라이선스&저작권",
                                onClick = { onEvent(SettingUiEvent.ClickLicenses) }
                            )
                            AppInfoRow(
                                title = "버전 정보",
                                rightText = "v${BuildConfig.VERSION_NAME}",
                                onClick = null
                            )
                        }
                    }
                }

                // DEV 옵션 빌드 버전 사용
                if (BuildConfig.DEBUG) {
                    item {
                        Column {
                            SectionTitle("DEV 옵션")
                            Spacer(Modifier.height(8.dp))

                            DevProMembershipToggle(
                                enabled = uiState.isProMember,
                                onToggle = { onEvent(SettingUiEvent.ToggleProMembership(it)) }
                            )
                        }
                    }
                }
            }

            // 다이얼로그 처리
            when (val dialog = uiState.dialogState) {
                is SettingDialogState.ConfirmSwitch -> {
                    ConfirmDialog(
                        title = "계정 전환",
                        message = "${dialog.targetAccount.gameName}\n이 계정으로 전환하시겠습니까?",
                        confirmText = "전환",
                        dismissText = "취소",
                        onConfirm = { onEvent(SettingUiEvent.ConfirmDialog) },
                        onDismiss = { onEvent(SettingUiEvent.DismissDialog) }
                    )
                }

                is SettingDialogState.ConfirmLogout -> {
                    ConfirmDialog(
                        title = "로그아웃",
                        message = "${dialog.targetAccount.gameName}\n이 계정을 로그아웃하시겠습니까?",
                        confirmText = "로그아웃",
                        dismissText = "취소",
                        onConfirm = { onEvent(SettingUiEvent.ConfirmDialog) },
                        onDismiss = { onEvent(SettingUiEvent.DismissDialog) }
                    )
                }

                is SettingDialogState.DevLoginPrompt -> {
                    ConfirmDialog(
                        title = "임시 로그인",
                        message = if (dialog.isAddAccount) {
                            "현재 계정 추가 기능이 준비 중입니다.\n임시 로그인으로 계정을 추가하시겠습니까?"
                        } else {
                            "현재 로그인 기능이 준비 중입니다.\n임시 로그인을 진행하시겠습니까?"
                        },
                        confirmText = "임시 로그인",
                        dismissText = "취소",
                        onConfirm = { onEvent(SettingUiEvent.ConfirmDialog) },
                        onDismiss = { onEvent(SettingUiEvent.DismissDialog) }
                    )
                }

                null -> Unit
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = TextWhite
    )
}

@Composable
private fun DevProMembershipToggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "프로 멤버십 (DEV)",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "개발 단계 테스트용 토글",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}
