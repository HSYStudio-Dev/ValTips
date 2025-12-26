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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsystudio.valtips.feature.lineup.ui.component.LineupHeaderCard
import com.hsystudio.valtips.feature.lineup.viewmodel.AgentSelectViewModel
import com.hsystudio.valtips.ui.component.AgentCard
import com.hsystudio.valtips.ui.component.BorderButton
import com.hsystudio.valtips.ui.component.IconChip
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.TextGray

@Composable
fun AgentSelectScreen(
    onBack: () -> Unit,
    onAgentClick: (agentUuid: String, mapUuid: String) -> Unit,
    viewModel: AgentSelectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val mapUuid = viewModel.mapUuid

    // 그리드 스크롤 상태
    val gridState = rememberLazyGridState()

    // 역할이 바뀔 때마다 스크롤 맨 위로 이동
    LaunchedEffect(uiState.selectedRoleUuid) {
        // 애니메이션 없이 즉시 이동
        gridState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "SELECT Agent",
                onNavClick = onBack
            )
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
                Spacer(Modifier.height(verticalPadding))

                // 헤더 카드
                LineupHeaderCard(
                    agentIcon = null,
                    mapSplash = uiState.mapSplashLocal,
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
                                    onClick = { viewModel.getAgentLineupStatus() }
                                )
                            }
                        }
                    }

                    else -> {
                        val noLineupAvailable = uiState.lineupStatus.isNotEmpty() &&
                            uiState.lineupStatus.values.none { it }

                        // 선택한 맵 기준 라인업이 있는 요원이 하나도 없는 경우
                        if (noLineupAvailable) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "해당 맵은 라인업을 등록한 요원이 아직 없습니다.",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // 라인업이 있는 요원이 하나 이상인 경우
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                                ) {
                                    items(count = minOf(5, uiState.roles.size)) { i ->
                                        val item = uiState.roles[i]
                                        IconChip(
                                            isSelected = (uiState.selectedRoleUuid ?: "") == item.uuid,
                                            isAll = item.uuid.isEmpty(),
                                            iconLocal = item.roleIconLocal,
                                            onClick = { viewModel.selectRole(item.uuid) }
                                        )
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                // 요원 카드 그리드
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 64.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(bottom = verticalPadding),
                                    modifier = Modifier.weight(1f),
                                    state = gridState
                                ) {
                                    items(
                                        items = uiState.agents,
                                        key = { it.uuid }
                                    ) { agent ->
                                        val hasLineups = uiState.lineupStatus[agent.uuid] ?: false
                                        Box(
                                            modifier = Modifier.animateItem()
                                        ) {
                                            AgentCard(
                                                agent = agent,
                                                onClick = {
                                                    onAgentClick(agent.uuid, mapUuid)
                                                },
                                                enabled = hasLineups
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
