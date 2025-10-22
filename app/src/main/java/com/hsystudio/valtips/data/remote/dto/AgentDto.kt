package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgentDto(
    val uuid: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("origin_country")
    val originCountry: String? = null,
    val description: String? = null,
    @SerialName("display_icon")
    val displayIcon: String? = null,
    @SerialName("full_portrait")
    val fullPortrait: String? = null,
    val id: Int,
    val role: RoleDto,
    val abilities: List<AbilityDto> = emptyList()
)

@Serializable
data class RoleDto(
    val uuid: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("display_icon")
    val displayIcon: String? = null,
    val id: Int
)

@Serializable
data class AbilityDto(
    val slot: String,
    @SerialName("display_name")
    val displayName: String,
    val description: String? = null,
    @SerialName("display_icon")
    val displayIcon: String? = null,
    val details: String? = null,
    val id: Int,
    @SerialName("agent_id")
    val agentId: Int
)
