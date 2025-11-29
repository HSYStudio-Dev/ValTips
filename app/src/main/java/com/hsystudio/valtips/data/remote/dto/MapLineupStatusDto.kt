package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MapLineupStatusDto(
    val id: Int,
    val uuid: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("english_name")
    val englishName: String? = null,
    @SerialName("list_view_icon")
    val listViewIcon: String? = null,
    @SerialName("has_lineups")
    val hasLineups: Boolean
)
