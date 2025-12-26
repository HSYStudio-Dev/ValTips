package com.hsystudio.valtips.feature.agent.ui

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
import com.hsystudio.valtips.feature.agent.viewmodel.AgentsViewModel
import com.hsystudio.valtips.ui.component.AgentCard
import com.hsystudio.valtips.ui.component.BorderButton
import com.hsystudio.valtips.ui.component.IconChip
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.TextGray

@Composable
fun AgentsScreen(
    onAgentClick: (String) -> Unit,
    viewModel: AgentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 그리드 스크롤 상태
    val gridState = rememberLazyGridState()

    // 역할이 바뀔 때마다 스크롤 맨 위로 이동
    LaunchedEffect(uiState.selectedRoleUuid) {
        // 애니메이션 없이 즉시 이동
        gridState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Agents")
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
                                text = uiState.error ?: "일부 리소스를 불러오지 못했습니다.\n문제가 계속 된다면 설정 탭에서\n최신 리소스 다운로드를 진행해 주세요.",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextGray,
                                textAlign = TextAlign.Center
                            )
                            BorderButton(
                                text = "새로고침",
                                onClick = { viewModel.selectRole("") }
                            )
                        }
                    }
                }

                // 정상
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding)
                            .padding(top = verticalPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // 역할 필터 버튼
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
                                Box(
                                    modifier = Modifier.animateItem()
                                ) {
                                    AgentCard(
                                        agent = agent,
                                        onClick = onAgentClick
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
