package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LineupStatusDto(
    val uuid: String,
    @SerialName("has_lineups")
    val hasLineups: Boolean
)
