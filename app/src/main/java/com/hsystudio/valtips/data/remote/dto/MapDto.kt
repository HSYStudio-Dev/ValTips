package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MapDto(
    val uuid: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("english_name") val englishName: String? = null,
    @SerialName("tactical_description") val tacticalDescription: String? = null,
    @SerialName("list_view_icon") val listViewIcon: String? = null,
    val splash: String? = null,
    @SerialName("display_icon_attacker") val displayIconAttacker: String? = null,
    @SerialName("display_icon_defender") val displayIconDefender: String? = null,
    @SerialName("display_icon_attacker_smoke") val displayIconAttackerSmoke: String? = null,
    @SerialName("display_icon_defender_smoke") val displayIconDefenderSmoke: String? = null,
    @SerialName("is_active_in_rotation") val isActiveInRotation: Boolean? = null,
    val id: Int,
    @SerialName("recommended_agent_1_id") val recAgent1Id: String? = null,
    @SerialName("recommended_agent_2_id") val recAgent2Id: String? = null,
    @SerialName("recommended_agent_3_id") val recAgent3Id: String? = null,
    @SerialName("recommended_agent_4_id") val recAgent4Id: String? = null,
    @SerialName("recommended_agent_5_id") val recAgent5Id: String? = null
)
