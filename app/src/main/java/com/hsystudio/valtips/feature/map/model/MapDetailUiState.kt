package com.hsystudio.valtips.feature.map.model

// 맵 상세 화면 UI 상태
data class MapDetailUiState(
    val uuid: String,
    val displayName: String,
    val englishName: String?,
    val tacticalDescription: String?,
    val splashLocal: String?,
    val miniMapAttackerLocal: String?,
    val miniMapDefenderLocal: String?,
    val miniMapAttackerSmokeLocal: String?,
    val miniMapDefenderSmokeLocal: String?,
    val recommendedAgents: List<MapRecommendedAgentItem>,

    // 미니맵 출력 플래그
    val hasAttackerView: Boolean,
    val hasDefenderView: Boolean,
    val hasAttackerSmoke: Boolean,
    val hasDefenderSmoke: Boolean
)

// 추천 요원 아이콘
data class MapRecommendedAgentItem(
    val uuid: String,
    val iconLocal: String?
)
