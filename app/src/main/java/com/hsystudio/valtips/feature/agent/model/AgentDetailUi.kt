package com.hsystudio.valtips.feature.agent.model

// 요원 상세 화면에서 사용할 데이터 구조
data class AgentDetailUi(
    val uuid: String,
    val name: String,
    val roleName: String?,
    val origin: String?,
    val description: String?,
    val portraitLocal: String?,
    val iconLocal: String?,
    val roleIconLocal: String?,
    val abilities: List<AbilityUi>
)

// 스킬에 대한 UI 데이터 구조
data class AbilityUi(
    val slot: String,
    val name: String,
    val iconLocal: String?,
    val description: String?,
    val details: String?
)
