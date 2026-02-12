package com.hsystudio.valtips.feature.lineup.model

// 라인업 상세 한 단계에 대한 UI 모델
data class LineupStepItem(
    val stepNumber: Int,
    val description: String,
    val imageUrl: String
)

// 라인업 상세에 대한 UI 모델
data class LineupDetailItem(
    val id: Int,
    val title: String,
    val description: String,
    val side: LineupSideFilter,
    val site: String,
    val writer: String,
    val updatedAt: String,
    val steps: List<LineupStepItem>
)
