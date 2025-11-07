package com.hsystudio.valtips.domain.model

// 맵 카드 표시용 도메인 모델
data class MapListItem(
    val uuid: String,
    val displayName: String,
    val englishName: String?,
    val listImageLocal: String?,
    val isActiveInRotation: Boolean
)

// 시즌 활성/제외 맵 목록 UI 상태
data class MapsUiState(
    val actTitle: String? = null,
    val activeMaps: List<MapListItem> = emptyList(),
    val retiredMaps: List<MapListItem> = emptyList()
)
