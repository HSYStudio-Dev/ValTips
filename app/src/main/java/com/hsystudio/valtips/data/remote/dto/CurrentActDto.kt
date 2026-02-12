package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentActDto(
    @SerialName("id")
    val id: Int,
    @SerialName("display_name")
    val displayName: String
)
