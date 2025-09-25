package com.hsystudio.valtips.feature.login

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsystudio.valtips.R
import com.hsystudio.valtips.ui.theme.ColorBG
import com.hsystudio.valtips.ui.theme.ColorRed
import kotlinx.coroutines.delay

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SplashScreen(
    onNavigateToOnBoarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
        delay(2000)

        // 온보딩 완료 여부 확인 (임시)
        val isOnboardingCompleted = false
        // 로그인 여부 확인 (임시)
        val isLoggedIn = false
        // 분기 조건
        when {
            !isOnboardingCompleted -> onNavigateToOnBoarding()
            isOnboardingCompleted && !isLoggedIn -> onNavigateToLogin()
            isOnboardingCompleted && isLoggedIn -> onNavigateToHome()
        }
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
