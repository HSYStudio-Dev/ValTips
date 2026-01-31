package com.hsystudio.valtips.feature.lineup.model

import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.domain.model.RoleFilterItem

// 요원 선택 화면 UI 상태
data class AgentSelectUiState(
    val roles: List<RoleFilterItem> = emptyList(),
    val selectedRoleUuid: String? = null,
    val agents: List<AgentCardItem> = emptyList(),
    val lineupStatus: Map<String, Boolean> = emptyMap(),
    val mapSplashLocal: String? = null,
    val placeholderIconLocal: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isProMember: Boolean = false
)
