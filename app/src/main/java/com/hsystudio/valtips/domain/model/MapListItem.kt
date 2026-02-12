package com.hsystudio.valtips.domain.model

// 맵 카드 표시용 도메인 모델
data class MapListItem(
    val uuid: String,
    val displayName: String,
    val englishName: String?,
    val listImageLocal: String?,
    val isActiveInRotation: Boolean
)
