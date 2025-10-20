package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatesInfoDto(
    @SerialName("latest_timestamp")
    val latestTimestamp: String,
    @SerialName("update_counts")
    val updateCounts: UpdateCountsDto
)

@Serializable
data class UpdateCountsDto(
    val agents: Int,
    val maps: Int,
    val tiers: Int,
    @SerialName("game_modes")
    val gameModes: Int
)
