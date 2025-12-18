package com.hsystudio.valtips.feature.lineup.model

// 라인업 목록 상단 필터용 사이드 구분
enum class LineupSideFilter {
    ALL,
    ATTACK,
    DEFENSE
}

data class LineupCardBase(
    val id: Int,
    val title: String,
    val writer: String,
    val side: LineupSideFilter,
    val thumbnail: String?,
    val abilitySlotUi: String,
    val abilitySlotRaw: String,
    val agentUuid: String
)

// 라인업 카드 한 개에 대한 UI 모델
data class LineupCardItem(
    val id: Int,
    val title: String,
    val writer: String,
    val side: LineupSideFilter,
    val thumbnail: String?,
    val abilitySlot: String,
    val agentImage: String?,
    val abilityImage: String?
)
