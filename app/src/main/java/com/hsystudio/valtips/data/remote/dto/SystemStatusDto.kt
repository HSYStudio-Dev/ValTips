package com.hsystudio.valtips.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemStatusDto(
    @SerialName("is_maintenance")
    val isMaintenance: Boolean,
    @SerialName("maintenance_message")
    val maintenanceMessage: String
)
