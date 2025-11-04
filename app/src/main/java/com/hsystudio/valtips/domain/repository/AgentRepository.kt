package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.domain.model.RoleFilterItem
import kotlinx.coroutines.flow.Flow

interface AgentRepository {
    // 역할별 요원 목록 실시간 관찰
    fun observeAgents(roleUuid: String?): Flow<List<AgentCardItem>>

    // 역할 필터 목록 조회
    suspend fun getRoleFilters(): List<RoleFilterItem>
}
