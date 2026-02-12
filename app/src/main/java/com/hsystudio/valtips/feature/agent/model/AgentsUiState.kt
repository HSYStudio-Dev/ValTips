package com.hsystudio.valtips.feature.agent.model

import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.domain.model.RoleFilterItem

data class AgentsUiState(
    val isLoading: Boolean = true,
    val roles: List<RoleFilterItem> = emptyList(),
    val agents: List<AgentCardItem> = emptyList(),
    val selectedRoleUuid: String? = null,
    val error: String? = null,
    val isProMember: Boolean = false
)
