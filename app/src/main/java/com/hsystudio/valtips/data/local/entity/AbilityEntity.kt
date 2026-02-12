package com.hsystudio.valtips.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "abilities",
    foreignKeys = [
        ForeignKey(
            entity = AgentEntity::class,
            parentColumns = ["uuid"],
            childColumns = ["agentUuid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["agentUuid"])
    ]
)
data class AbilityEntity(
    @PrimaryKey val id: Int,
    val agentUuid: String,
    val slot: String,
    val displayName: String,
    val description: String?,
    val displayIconUrl: String?,
    val displayIconLocal: String?,
    val details: String?
)
