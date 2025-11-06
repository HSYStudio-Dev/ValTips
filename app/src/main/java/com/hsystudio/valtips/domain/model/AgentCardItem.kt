package com.hsystudio.valtips.domain.model

// 요원 카드 표시용 도메인 모델
data class AgentCardItem(
    val uuid: String,
    val roleUuid: String,
    val agentIconLocal: String?
)

// 역할 필터 표시용 도메인 모델
data class RoleFilterItem(
    val uuid: String,
    val roleIconLocal: String?
)
