package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TierDto(
    val tier: Int,
    @SerialName("tier_name") val tierName: String,
    val color: String? = null,
    @SerialName("large_icon") val largeIcon: String? = null,
    val id: Int
)
