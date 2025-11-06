package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.data.local.dao.AgentDao
import com.hsystudio.valtips.data.local.dao.RoleDao
import com.hsystudio.valtips.data.mapper.toAgentCardItem
import com.hsystudio.valtips.data.mapper.toAgentDetailUi
import com.hsystudio.valtips.data.mapper.toRoleFilterItems
import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.domain.model.RoleFilterItem
import com.hsystudio.valtips.feature.agent.model.AgentDetailUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AgentRepositoryImpl @Inject constructor(
    private val agentDao: AgentDao,
    private val roleDao: RoleDao
) : AgentRepository {
    // 역할별 요원 목록 실시간 관찰
    override fun observeAgents(roleUuid: String?): Flow<List<AgentCardItem>> =
        agentDao.observeAllWithDetails().map { list ->
            list.asSequence()
                .filter { roleUuid == null || it.role.uuid == roleUuid }
                .map { it.toAgentCardItem() }
                .toList()
        }

    // 역할 필터 목록 조회
    override suspend fun getRoleFilters(): List<RoleFilterItem> =
        roleDao.getAll().toRoleFilterItems()

    // 요원 상세 정보 실시간 관찰
    override fun observeAgentDetail(agentUuid: String): Flow<AgentDetailUi> =
        agentDao.observeWithDetails(agentUuid).map { rel ->
            requireNotNull(rel) { "Agent not found: $agentUuid" }.toAgentDetailUi()
        }
}
