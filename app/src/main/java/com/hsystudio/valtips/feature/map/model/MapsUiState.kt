package com.hsystudio.valtips.feature.map.model

import com.hsystudio.valtips.domain.model.MapListItem

data class MapsUiState(
    val isLoading: Boolean = true,
    val actTitle: String? = null,
    val activeMaps: List<MapListItem> = emptyList(),
    val retiredMaps: List<MapListItem> = emptyList(),
    val error: String? = null
)
