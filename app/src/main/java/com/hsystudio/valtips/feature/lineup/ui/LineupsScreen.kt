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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsystudio.valtips.feature.lineup.model.LineupSideFilter
import com.hsystudio.valtips.feature.lineup.ui.component.LineupHeaderCard
import com.hsystudio.valtips.feature.lineup.viewmodel.LineupsViewModel
import com.hsystudio.valtips.ui.component.BorderButton
import com.hsystudio.valtips.ui.component.IconChip
import com.hsystudio.valtips.ui.component.LineupCard
import com.hsystudio.valtips.ui.component.SegmentItem
import com.hsystudio.valtips.ui.component.SegmentedControl
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.TextGray

@Composable
fun LineupsScreen(
    onBack: () -> Unit,
    onLineupClick: (Int) -> Unit,
    viewModel: LineupsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    // 상단 사이드 필터 상태 (전체 / 공격 / 수비)
    var sideFilter by remember { mutableStateOf(LineupSideFilter.ALL) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "LINEUPS",
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(verticalPadding))

                // 헤더 카드
                LineupHeaderCard(
                    agentIcon = uiState.agentIconLocal,
                    mapSplash = uiState.mapSplashLocal,
                    placeholderIcon = uiState.placeholderIconLocal,
                    boxHeight = boxHeight
                )

                Spacer(Modifier.height(16.dp))

                // 사이드 필터 (전체 / 공격 / 수비)
                SegmentedControl(
                    items = listOf(
                        SegmentItem(
                            value = LineupSideFilter.ALL,
                            label = "전체",
                            gradientSpec = { dark, mint -> Triple(mint, null, dark) }
                        ),
                        SegmentItem(
                            value = LineupSideFilter.ATTACK,
                            label = "공격",
                            gradientSpec = { dark, mint -> Triple(dark, mint, dark) }
                        ),
                        SegmentItem(
                            value = LineupSideFilter.DEFENSE,
                            label = "수비",
                            gradientSpec = { dark, mint -> Triple(dark, null, mint) }
                        )
                    ),
                    selected = sideFilter,
                    onSelected = { sideFilter = it },
                    height = 52.dp,
                    outerRadius = 16.dp,
                    innerRadius = 12.dp
                )

                Spacer(Modifier.height(12.dp))

                // 3) 스킬 필터
                if (uiState.abilities.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(
                            items = uiState.abilities,
                            key = { ab -> "${ab.slot}-${ab.name}" }
                        ) { ability ->
                            IconChip(
                                isSelected = uiState.selectedAbilitySlot == ability.slot,
                                isAll = false,
                                iconLocal = ability.iconLocal,
                                btnSize = 64.dp,
                                iconSize = 48.dp,
                                onClick = {
                                    viewModel.abilityFilter(ability.slot)
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 상태 분기 (로딩 / 에러 / 리스트)
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
                                    onClick = { viewModel.getLineups() }
                                )
                            }
                        }
                    }

                    else -> {
                        // 사이드 필터 적용
                        val filteredBySide = when (sideFilter) {
                            LineupSideFilter.ALL -> uiState.lineups
                            LineupSideFilter.ATTACK ->
                                uiState.lineups.filter { it.side == LineupSideFilter.ATTACK }
                            LineupSideFilter.DEFENSE ->
                                uiState.lineups.filter { it.side == LineupSideFilter.DEFENSE }
                        }

                        // 공격/수비 분리
                        val attackList = filteredBySide.filter { it.side == LineupSideFilter.ATTACK }
                        val defenseList = filteredBySide.filter { it.side == LineupSideFilter.DEFENSE }

                        // 등록된 라인업이 없는 경우
                        if (attackList.isEmpty() && defenseList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "등록된 라인업이 없습니다.",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // 등록된 라인업이 있는 경우
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (attackList.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "공격(${attackList.size})",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = TextGray,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    }
                                    items(attackList, key = { it.id }) { item ->
                                        LineupCard(
                                            item = item,
                                            onClick = { onLineupClick(item.id) }
                                        )
                                    }
                                }

                                if (defenseList.isNotEmpty()) {
                                    item {
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = "수비(${defenseList.size})",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = TextGray,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    }
                                    items(defenseList, key = { it.id }) { item ->
                                        LineupCard(
                                            item = item,
                                            onClick = { onLineupClick(item.id) }
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
