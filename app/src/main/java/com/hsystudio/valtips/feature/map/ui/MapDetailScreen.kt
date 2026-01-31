package com.hsystudio.valtips.feature.map.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.feature.map.ui.component.MapInfoSection
import com.hsystudio.valtips.feature.map.ui.component.MiniMapSection
import com.hsystudio.valtips.feature.map.ui.component.RecommendedAgentsSection
import com.hsystudio.valtips.feature.map.viewmodel.MapDetailViewModel
import com.hsystudio.valtips.ui.component.BorderButton
import com.hsystudio.valtips.ui.component.ad.AdErrorPlaceholder
import com.hsystudio.valtips.ui.component.ad.AdLoadingPlaceholder
import com.hsystudio.valtips.ui.component.ad.NativeAdBanner
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.TextGray

@Composable
fun MapDetailScreen(
    onBack: () -> Unit,
    onGuideClick: (String) -> Unit,
    viewModel: MapDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val adState by viewModel.nativeAdState.collectAsStateWithLifecycle()
    val isProMember by viewModel.isProMember.collectAsStateWithLifecycle()

    // 스크롤 상태
    val scroll = rememberScrollState()
    // 공격/수비 선택 상태
    var isAttackerView by remember { mutableStateOf(true) }
    // 연막 스위치 상태
    var showSmoke by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Details",
                onNavClick = onBack
            )
        }
    ) { values ->
        when (val data = uiState) {
            null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = values.calculateTopPadding()),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TextGray)
            }

            else -> {
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
                    val agentCardSize = when {
                        maxWidth < 600.dp -> 48.dp
                        else -> 64.dp
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll)
                            .padding(horizontal = horizontalPadding)
                            .padding(bottom = verticalPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(Modifier.height(16.dp))

                        // 맵 이름 + 사이트 설명
                        MapInfoSection(data)

                        Spacer(Modifier.height(16.dp))

                        // 추천 요원
                        RecommendedAgentsSection(
                            agents = data.recommendedAgents,
                            size = agentCardSize
                        )

                        // 광고 배너
                        if (!isProMember) {
                            Spacer(Modifier.height(8.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                when (adState) {
                                    is NativeAdUiState.Loading -> {
                                        AdLoadingPlaceholder()
                                    }
                                    is NativeAdUiState.Error -> {
                                        AdErrorPlaceholder()
                                    }
                                    is NativeAdUiState.Success -> {
                                        NativeAdBanner(
                                            nativeAd = (adState as NativeAdUiState.Success).ad,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // 미니맵 + 토글
                        MiniMapSection(
                            data = data,
                            isAttackerView = isAttackerView,
                            showSmoke = showSmoke,
                            onSideChange = { isAttackerView = it },
                            onSmokeChange = { showSmoke = it }
                        )

                        Spacer(Modifier.height(16.dp))

                        // 요원별 스킬 가이드 버튼
                        BorderButton(
                            text = "요원별 스킬 가이드",
                            onClick = { onGuideClick(data.uuid) }
                        )
                    }
                }
            }
        }
    }
}
