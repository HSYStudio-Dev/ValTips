package com.hsystudio.valtips.feature.lineup.model

import com.hsystudio.valtips.feature.agent.model.AbilityItem

data class LineupsUiState(
    val lineups: List<LineupCardItem> = emptyList(),
    val abilities: List<AbilityItem> = emptyList(),
    val selectedAbilitySlot: String? = null,
    val agentIconLocal: String? = null,
    val mapSplashLocal: String? = null,
    val placeholderIconLocal: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
