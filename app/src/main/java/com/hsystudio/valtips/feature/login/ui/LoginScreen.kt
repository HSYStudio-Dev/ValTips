package com.hsystudio.valtips.feature.login.ui

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.hsystudio.valtips.R
import com.hsystudio.valtips.feature.login.ui.dialog.HelpDialog
import com.hsystudio.valtips.feature.login.viewmodel.LoginViewModel
import com.hsystudio.valtips.ui.component.BorderButton
import com.hsystudio.valtips.ui.theme.TextBlack
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val portraitUrls by viewModel.portraitUrls.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) }

    // 안내 사항 다이얼로그 표시 상태
    var showHelp by remember { mutableStateOf(false) }

    // 에러 메시지 토스트 출력
    LaunchedEffect(Unit) {
        viewModel.errorEvents.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // 요원 이미지 자동 전환
    LaunchedEffect(portraitUrls) {
        if (portraitUrls.isNotEmpty()) {
            while (true) {
                delay(3000)
                currentIndex = (currentIndex + 1) % portraitUrls.size
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
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
        val titleSize = when {
            maxHeight < 600.dp -> 56.sp
            maxHeight < 800.dp -> 72.sp
            else -> 96.sp
        }
        // 배경
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F1924))
        ) {
            // 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.login_bg),
                contentDescription = "배경 이미지",
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.FillWidth,
                alpha = 0.4f
            )
            // 요원 이미지
            if (portraitUrls.isNotEmpty()) {
                Crossfade(
                    targetState = currentIndex,
                    animationSpec = tween(1000),
                    modifier = Modifier.fillMaxSize()
                ) { index ->
                    AsyncImage(
                        model = portraitUrls[index],
                        contentDescription = "요원 이미지",
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(3f / 4f),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // 첫 로딩 중 고정 이미지
                Image(
                    painter = painterResource(R.drawable.agent_jett),
                    contentDescription = "요원 이미지(고정)",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .aspectRatio(3f / 4f),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 전체 Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding)
                .padding(top = verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 타이틀
            Text(
                text = "vAlTips",
                style = MaterialTheme.typography.displayLarge.copy(
                    shadow = Shadow(
                        color = TextBlack.copy(alpha = 0.5f),
                        offset = with(density) { Offset(0f, 5.dp.toPx()) },
                        blurRadius = with(density) { 5.dp.toPx() }
                    )
                ),
                fontSize = titleSize,
                color = TextWhite,
                modifier = Modifier.padding(top = verticalPadding)
            )

            // 하단 Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 로그인 버튼
                BorderButton(
                    text = "Riot ID로 로그인",
                    onClick = {
                        onNavigateToHome()  // Todo: RSO 연동 후 수정
                    }
                )
                Spacer(Modifier.height(16.dp))
                // 안내 문구
                Text(
                    text = "로그인 시 프로필이 공개 설정됩니다.\n자세한 내용은 [안내 사항]을 눌러 확인해 주세요!",
                    color = TextGray,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
                // 도움말 버튼
                TextButton(
                    onClick = { showHelp = true }
                ) {
                    Text(
                        text = "안내 사항",
                        color = TextWhite,
                        style = MaterialTheme.typography.bodySmall,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
    // 안내 사항 다이얼로그 연결
    if (showHelp) {
        HelpDialog(
            onDismiss = { showHelp = false }
        )
    }
}
