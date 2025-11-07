package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MapDto(
    val uuid: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("english_name") val englishName: String? = null,
    @SerialName("tactical_description") val tacticalDescription: String? = null,
    @SerialName("display_icon") val displayIcon: String? = null,
    @SerialName("list_view_icon") val listViewIcon: String? = null,
    val splash: String? = null,
    @SerialName("is_active_in_rotation") val isActiveInRotation: Boolean? = null,
    @SerialName("x_multiplier") val xMultiplier: Double? = null,
    @SerialName("y_multiplier") val yMultiplier: Double? = null,
    @SerialName("x_scalar_to_add") val xScalarToAdd: Double? = null,
    @SerialName("y_scalar_to_add") val yScalarToAdd: Double? = null,
    @SerialName("recommended_agent_1_id") val recommendedAgent1Id: String? = null,
    @SerialName("recommended_agent_2_id") val recommendedAgent2Id: String? = null,
    @SerialName("recommended_agent_3_id") val recommendedAgent3Id: String? = null,
    @SerialName("recommended_agent_4_id") val recommendedAgent4Id: String? = null,
    @SerialName("recommended_agent_5_id") val recommendedAgent5Id: String? = null,
    val id: Int,
    val callouts: List<MapCalloutDto> = emptyList()
)

@Serializable
data class MapCalloutDto(
    @SerialName("region_name") val regionName: String,
    @SerialName("super_region_name") val superRegionName: String,
    @SerialName("community_name") val communityName: String? = null,
    val location: MapLocationDto,
    val id: Int,
    @SerialName("map_id") val mapId: Int
)

@Serializable
data class MapLocationDto(
    val x: Double,
    val y: Double
)
