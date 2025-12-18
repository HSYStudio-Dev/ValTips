package com.hsystudio.valtips.feature.agent.model

// 요원 상세 화면 UI 상태
data class AgentDetailUiState(
    val uuid: String,
    val name: String,
    val roleName: String?,
    val origin: String?,
    val description: String?,
    val portraitLocal: String?,
    val iconLocal: String?,
    val roleIconLocal: String?,
    val abilities: List<AbilityItem>
)

// 스킬에 대한 UI 데이터 구조
data class AbilityItem(
    val slot: String,
    val name: String,
    val iconLocal: String?,
    val description: String?,
    val details: String?
)
