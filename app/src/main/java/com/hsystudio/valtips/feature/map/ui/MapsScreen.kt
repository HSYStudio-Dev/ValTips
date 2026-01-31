package com.hsystudio.valtips.feature.map.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.feature.map.viewmodel.MapsViewModel
import com.hsystudio.valtips.ui.component.MapCard
import com.hsystudio.valtips.ui.component.ad.AdErrorPlaceholder
import com.hsystudio.valtips.ui.component.ad.AdLoadingPlaceholder
import com.hsystudio.valtips.ui.component.ad.NativeAdBanner
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.ColorStroke
import com.hsystudio.valtips.ui.theme.TextGray

@Composable
fun MapsScreen(
    onMapClick: (String) -> Unit,
    viewModel: MapsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val adState by viewModel.nativeAdState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 탑바
                AppTopBar(title = "Maps")

                // 광고 영역 (프로 멤버 아닐 때만)
                if (uiState.isProMember.not()) {
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

                    HorizontalDivider(
                        color = ColorStroke,
                        thickness = 1.dp
                    )
                }
            }
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
            // 상태별 분기 (로딩 / 에러 / 정상)
            when {
                // 로딩 상태
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TextGray)
                    }
                }

                // 에러 상태
                uiState.error != null -> {
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
                                text = uiState.error ?: "맵 리소스를 불러오지 못했습니다.\n문제가 계속 된다면 설정 탭에서\n최신 리소스 다운로드를 진행해 주세요.",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // 정상
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding),
                        contentPadding = PaddingValues(vertical = verticalPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 섹션: 현재 액트
                        if (uiState.activeMaps.isNotEmpty()) {
                            item {
                                uiState.actTitle?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            textDecoration = TextDecoration.Underline
                                        ),
                                        color = TextGray,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            // 시즌 활성화 맵
                            items(
                                items = uiState.activeMaps,
                                key = { it.uuid }
                            ) { map ->
                                MapCard(
                                    item = map,
                                    active = true,
                                    onClick = onMapClick
                                )
                            }
                        }

                        // 섹션: 제외된 맵
                        if (uiState.retiredMaps.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "제외된 맵",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        textDecoration = TextDecoration.Underline
                                    ),
                                    color = TextGray,
                                    textAlign = TextAlign.Left,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            // 시즌 비활성화 맵
                            items(
                                items = uiState.retiredMaps,
                                key = { it.uuid }
                            ) { map ->
                                MapCard(
                                    item = map,
                                    active = false,
                                    onClick = onMapClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
