package com.hsystudio.valtips.feature.lineup.model

// 라인업 상세 화면 UI 상태
data class LineupDetailUiState(
    val detail: LineupDetailItem? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
