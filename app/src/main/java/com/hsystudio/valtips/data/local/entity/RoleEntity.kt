package com.hsystudio.valtips.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roles")
data class RoleEntity(
    @PrimaryKey val uuid: String,
    val displayName: String,
    val displayIconUrl: String?,
    val displayIconLocal: String?,
    val numericId: Int
)
