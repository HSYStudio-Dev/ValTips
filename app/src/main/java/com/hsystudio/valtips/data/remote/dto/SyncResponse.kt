package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    val agents: List<AgentDto> = emptyList(),
    val maps: List<MapDto> = emptyList(),
    val tiers: List<TierDto> = emptyList(),
    @SerialName("game_modes")
    val gameModes: List<GameModeDto> = emptyList()
)
