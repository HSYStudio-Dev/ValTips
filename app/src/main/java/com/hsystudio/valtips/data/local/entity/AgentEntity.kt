package com.hsystudio.valtips.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "agents",
    foreignKeys = [
        ForeignKey(
            entity = RoleEntity::class,
            parentColumns = ["uuid"],
            childColumns = ["roleUuid"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [ Index(value = ["roleUuid"]) ]
)
data class AgentEntity(
    @PrimaryKey val uuid: String,
    val displayName: String,
    val originCountry: String?,
    val description: String?,
    val displayIconUrl: String?,
    val displayIconLocal: String?,
    val fullPortraitUrl: String?,
    val fullPortraitLocal: String?,
    val numericId: Int,
    val roleUuid: String
)
