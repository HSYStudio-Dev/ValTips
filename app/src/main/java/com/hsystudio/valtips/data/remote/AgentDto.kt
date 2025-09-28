package com.hsystudio.valtips.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgentResponse(
    @SerialName("status") val status: Int,
    @SerialName("data") val data: List<AgentDto>
)

@Serializable
data class AgentDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("displayName") val displayName: String,
    @SerialName("description") val description: String,
    @SerialName("displayIcon") val displayIcon: String? = null,
    @SerialName("fullPortrait") val fullPortrait: String? = null,
    @SerialName("role") val role: RoleDto? = null,
    @SerialName("abilities") val abilities: List<AbilityDto>? = null
)

@Serializable
data class RoleDto(
    @SerialName("displayName") val displayName: String,
    @SerialName("description") val description: String,
    @SerialName("displayIcon") val displayIcon: String
)

@Serializable
data class AbilityDto(
    @SerialName("slot") val slot: String,
    @SerialName("displayName") val displayName: String,
    @SerialName("description") val description: String,
    @SerialName("displayIcon") val displayIcon: String? = null
)
