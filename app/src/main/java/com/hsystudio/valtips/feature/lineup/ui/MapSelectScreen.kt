package com.hsystudio.valtips.feature.lineup.ui

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
import com.hsystudio.valtips.feature.lineup.ui.component.LineupHeaderCard
import com.hsystudio.valtips.feature.lineup.viewmodel.MapSelectViewModel
import com.hsystudio.valtips.ui.component.BorderButton
import com.hsystudio.valtips.ui.component.MapCard
import com.hsystudio.valtips.ui.component.ad.AdErrorPlaceholder
import com.hsystudio.valtips.ui.component.ad.AdLoadingPlaceholder
import com.hsystudio.valtips.ui.component.ad.NativeAdBanner
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.ColorStroke
import com.hsystudio.valtips.ui.theme.TextGray

@Composable
fun MapSelectScreen(
    onBack: () -> Unit,
    onMapClick: (agentUuid: String, mapUuid: String) -> Unit,
    viewModel: MapSelectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val agentUuid = viewModel.agentUuid
    val adState by viewModel.nativeAdState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 탑바
                AppTopBar(
                    title = "SELECT Map",
                    onNavClick = onBack
                )
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
            val boxHeight = when {
                maxWidth < 600.dp -> 48
                else -> 64
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
            ) {
                Spacer(Modifier.height(16.dp))

                // 헤더 카드
                LineupHeaderCard(
                    agentIcon = uiState.agentIconLocal,
                    mapSplash = null,
                    placeholderIcon = uiState.placeholderIconLocal,
                    boxHeight = boxHeight
                )

                Spacer(Modifier.height(16.dp))

                // 상태별 분기 (로딩 / 에러 / 리스트)
                when {
                    uiState.isLoading -> {
                        // 로딩 상태
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = TextGray)
                        }
                    }

                    uiState.error != null -> {
                        // 에러 상태
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = uiState.error ?: "라인업 정보를 불러오지 못했습니다.",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextGray,
                                    textAlign = TextAlign.Center
                                )
                                BorderButton(
                                    text = "다시 시도",
                                    onClick = { viewModel.getMapLineupStatus() }
                                )
                            }
                        }
                    }

                    else -> {
                        val noLineupAvailable = uiState.lineupStatus.isEmpty() ||
                            uiState.lineupStatus.values.none { it }

                        // 선택한 요원 기준 라인업이 있는 맵이 하나도 없는 경우
                        if (noLineupAvailable) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "해당 요원은 라인업을 등록한 맵이 아직 없습니다.",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // 라인업이 있는 맵이 하나 이상인 경우
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = verticalPadding),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // 액트 타이틀
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

                                    // 시즌 활성 맵
                                    items(
                                        items = uiState.activeMaps,
                                        key = { it.uuid }
                                    ) { map ->
                                        val hasLineups = uiState.lineupStatus[map.uuid] ?: false
                                        MapCard(
                                            item = map,
                                            active = hasLineups,
                                            enabled = hasLineups,
                                            onClick = { mapUuid ->
                                                onMapClick(agentUuid, mapUuid)
                                            }
                                        )
                                    }

                                    // 제외된 맵
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

                                        items(
                                            items = uiState.retiredMaps,
                                            key = { it.uuid }
                                        ) { map ->
                                            val hasLineups = uiState.lineupStatus[map.uuid] ?: false
                                            MapCard(
                                                item = map,
                                                active = hasLineups,
                                                enabled = hasLineups,
                                                onClick = { mapUuid ->
                                                    onMapClick(agentUuid, mapUuid)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
