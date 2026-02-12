package com.hsystudio.valtips.feature.lineup.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.feature.lineup.ui.component.LineupDetailHeaderCard
import com.hsystudio.valtips.feature.lineup.ui.component.LineupStepSection
import com.hsystudio.valtips.feature.lineup.viewmodel.LineupDetailViewModel
import com.hsystudio.valtips.ui.component.BorderButton
import com.hsystudio.valtips.ui.component.ad.AdErrorPlaceholder
import com.hsystudio.valtips.ui.component.ad.AdLoadingPlaceholder
import com.hsystudio.valtips.ui.component.ad.NativeAdBanner
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.ColorStroke
import com.hsystudio.valtips.ui.theme.TextGray

@Composable
fun LineupDetailScreen(
    onBack: () -> Unit,
    viewModel: LineupDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val adState by viewModel.nativeAdState.collectAsStateWithLifecycle()
    val isProMember by viewModel.isProMember.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Details",
                onNavClick = onBack
            )
        },
        bottomBar = {
            // 광고 영역 (프로 멤버 아닐 때만)
            if (!isProMember) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        color = ColorStroke,
                        thickness = 1.dp
                    )

                    Box {
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
            }
        }
    ) { values ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = values.calculateTopPadding(),
                    bottom = values.calculateBottomPadding()
                )
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
            // 상태별 분기 (로딩 / 에러 / 리스트)
            when {
                uiState.isLoading -> {
                    // 로딩 상태
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TextGray)
                    }
                }

                uiState.error != null -> {
                    // 에러 상태
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = uiState.error ?: "라인업 상세 정보를 불러오지 못했습니다.",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextGray,
                                textAlign = TextAlign.Center
                            )
                            BorderButton(
                                text = "다시 시도",
                                onClick = { viewModel.getLineupDetail() }
                            )
                        }
                    }
                }

                uiState.detail == null -> {
                    // 상세 조회 데이터가 없는 경우
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "상세 정보에 문제가 발생했습니다.\n문제가 계속된다면 관리자에게 문의하세요.",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    // 상세 조회 데이터가 정상인 경우
                    val detail = uiState.detail!!

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding),
                        contentPadding = PaddingValues(vertical = verticalPadding),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 상단 요약 카드
                        item {
                            LineupDetailHeaderCard(detail = detail)
                        }

                        // 단계 리스트
                        items(
                            items = detail.steps,
                            key = { step -> step.stepNumber }
                        ) { step ->
                            LineupStepSection(step = step)
                        }
                    }
                }
            }
        }
    }
}
