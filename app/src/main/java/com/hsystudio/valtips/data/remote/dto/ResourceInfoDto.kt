package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResourceInfoDto(
    @SerialName("total_size_megabytes")
    val totalSizeMegabytes: Double
)
