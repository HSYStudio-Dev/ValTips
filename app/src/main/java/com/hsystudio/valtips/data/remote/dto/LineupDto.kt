package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LineupDto(
    val id: Int,
    val title: String,
    val writer: String,
    val side: String,
    val thumbnail: String,
    @SerialName("ability_slot")
    val abilitySlot: String,
    @SerialName("agent_uuid")
    val agentUuid: String
)

@Serializable
data class LineupListResponseDto(
    @SerialName("latest_timestamp")
    val latestTimestamp: String,
    val data: List<LineupDto>
)
