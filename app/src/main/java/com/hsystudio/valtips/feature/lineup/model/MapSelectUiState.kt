package com.hsystudio.valtips.feature.lineup.model

import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.domain.model.MapListItem

// 맵 선택 화면 UI 상태
data class MapSelectUiState(
    val actTitle: String? = null,
    val activeMaps: List<MapListItem> = emptyList(),
    val retiredMaps: List<MapListItem> = emptyList(),
    val lineupStatus: Map<String, Boolean> = emptyMap(),
    val selectedAgent: AgentCardItem? = null,
    val placeholderIconLocal: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

// 맵 라인업 보유 여부 상태 모델
data class MapLineupStatus(
    val uuid: String,
    val hasLineups: Boolean
)
