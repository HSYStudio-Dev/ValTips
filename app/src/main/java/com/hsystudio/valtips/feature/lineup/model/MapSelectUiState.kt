package com.hsystudio.valtips.feature.lineup.model

import com.hsystudio.valtips.domain.model.MapListItem

// 맵 선택 화면 UI 상태
data class MapSelectUiState(
    val actTitle: String? = null,
    val activeMaps: List<MapListItem> = emptyList(),
    val retiredMaps: List<MapListItem> = emptyList(),
    val lineupStatus: Map<String, Boolean> = emptyMap(),
    val agentIconLocal: String? = null,
    val placeholderIconLocal: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isProMember: Boolean = false
)
