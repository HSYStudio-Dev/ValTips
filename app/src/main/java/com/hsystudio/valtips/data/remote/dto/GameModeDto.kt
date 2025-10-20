package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameModeDto(
    val uuid: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("display_icon") val displayIcon: String? = null,
    val id: Int
)
