package com.hsystudio.valtips.feature.login.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsystudio.valtips.R
import com.hsystudio.valtips.domain.model.TermsPolicy
import com.hsystudio.valtips.feature.login.model.Destination
import com.hsystudio.valtips.feature.login.ui.dialog.DownloadConfirmDialog
import com.hsystudio.valtips.feature.login.ui.dialog.MaintenanceDialog
import com.hsystudio.valtips.feature.login.ui.dialog.TermsConsentDialog
import com.hsystudio.valtips.feature.login.viewmodel.LoginViewModel
import com.hsystudio.valtips.ui.theme.ColorBG
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.util.openCustomTab
import kotlinx.coroutines.delay

@SuppressLint("UseOfNonLambdaOffsetOverload", "DefaultLocale")
@Composable
fun SplashScreen(
    onNavigateToOnBoarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onExitApp: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    BackHandler(enabled = true) { }

    val context = LocalContext.current
    val showMaintenanceDialog by viewModel.showMaintenanceDialog.collectAsStateWithLifecycle()
    val maintenanceMessage by viewModel.maintenanceMessage.collectAsStateWithLifecycle()
    val syncRunning by viewModel.syncRunning.collectAsStateWithLifecycle()
    val syncPhase by viewModel.syncPhase.collectAsStateWithLifecycle()
    val dialogVisible by viewModel.downloadDialogVisible.collectAsStateWithLifecycle()
    val sizeMb by viewModel.downloadSizeMb.collectAsStateWithLifecycle()
    val networkType by viewModel.networkType.collectAsStateWithLifecycle()
    val progressPercent by viewModel.progressPercent.collectAsStateWithLifecycle()
    val progressLabel by viewModel.progressLabel.collectAsStateWithLifecycle()
    val showReConsentDialog by viewModel.showPolicyReConsentDialog.collectAsStateWithLifecycle()

    // 에러 토스트
    LaunchedEffect(Unit) {
        viewModel.errorEvents.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // 종료 이벤트
    LaunchedEffect(Unit) {
        viewModel.exitApp.collect {
            delay(1800)
            onExitApp()
        }
    }

    // 네비게이션 처리
    LaunchedEffect(Unit) {
        viewModel.destination.collect { dest ->
            when (dest) {
                Destination.ONBOARDING -> onNavigateToOnBoarding()
                Destination.LOGIN -> onNavigateToLogin()
                Destination.HOME -> onNavigateToHome()
            }
        }
    }

    var startAnimation by remember { mutableStateOf(false) }

    // 로고 애니메이션
    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
        delay(1300)
        viewModel.runStartupFlow()
    }

    // 로고의 offset (Y축 위치)
    val logoOffsetY by animateDpAsState(
        targetValue = if (startAnimation) (-180).dp else 0.dp,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "logoOffsetY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBG)
    ) {
        // 로고
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.Center)
                .offset(y = logoOffsetY)
        )

        // 텍스트 애니메이션
        if (startAnimation) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                AnimatedSlideFadeIn(
                    text = "vAl",
                    size = 58,
                    fromY = -50f,
                    delayMillis = 400
                )
                AnimatedSlideFadeIn(
                    text = "Tips",
                    size = 64,
                    fromY = 50f,
                    delayMillis = 600
                )
            }
        }

        // 하단 로딩 영역
        if (syncRunning || dialogVisible) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (syncRunning) {
                    // 원형 로딩
                    CircularProgressIndicator(color = TextGray)

                    Spacer(Modifier.height(12.dp))
                    // 동기화 문구
                    Text(
                        text = syncPhase ?: "리소스를 준비 중입니다…",
                        color = TextGray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))
                    // 선형 로딩
                    LinearProgressIndicator(
                        progress = { progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = ColorMint,
                        trackColor = TextGray
                    )

                    Spacer(Modifier.height(6.dp))
                    // 다운로드 속도
                    if (progressLabel != null) {
                        Text(text = progressLabel!!)
                    }
                } else {
                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    }
    // 서버 점검 중 안내 다이얼로그
    if (showMaintenanceDialog) {
        MaintenanceDialog(
            message = maintenanceMessage ?: "현재 서버 점검 중입니다.",
            onExit = { viewModel.onConfirmMaintenance() }
        )
    }

    // 리소스 다운 동의 다이얼로그
    if (dialogVisible) {
        DownloadConfirmDialog(
            sizeMb = sizeMb,
            networkType = networkType,
            onConfirm = { viewModel.confirmInitialDownload() },
            onCancel = { viewModel.cancelInitialDownload() }
        )
    }
    // 약관 재동의 다이얼로그
    if (showReConsentDialog) {
        TermsConsentDialog(
            isReConsent = true,
            onOpenTerms = {
                openCustomTab(context, TermsPolicy.TERMS_URL)
            },
            onOpenPrivacy = {
                openCustomTab(context, TermsPolicy.PRIVACY_URL)
            },
            onConfirm = { viewModel.confirmReConsent() },
            onCancel = { viewModel.cancelReConsent() },
        )
    }
}

// 텍스트 애니메이션
@Composable
fun AnimatedSlideFadeIn(
    text: String,
    size: Int,
    fromY: Float,
    delayMillis: Int
) {
    var startAnimation by remember { mutableStateOf(false) }

    val offsetY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else fromY,
        animationSpec = tween(durationMillis = 600, delayMillis = delayMillis),
        label = "offsetY"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = delayMillis),
        label = "alpha"
    )

    LaunchedEffect(Unit) { startAnimation = true }

    Text(
        text = text,
        fontSize = size.sp,
        fontFamily = FontFamily(Font(R.font.valorant)),
        color = ColorRed,
        modifier = Modifier.graphicsLayer {
            translationY = offsetY
            this.alpha = alpha
        }
    )
}
