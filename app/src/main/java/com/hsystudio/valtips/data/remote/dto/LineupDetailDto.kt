package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LineupStepDto(
    @SerialName("step_number") val stepNumber: Int,
    val description: String,
    @SerialName("image_url") val imageUrl: String
)

@Serializable
data class LineupDetailDto(
    val title: String,
    val description: String,
    val side: String,
    val site: String,
    val writer: String,
    val id: Int,
    val steps: List<LineupStepDto>,
    @SerialName("updated_at") val updatedAt: String
)
