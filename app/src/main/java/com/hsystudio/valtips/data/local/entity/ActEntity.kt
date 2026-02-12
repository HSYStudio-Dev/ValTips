package com.hsystudio.valtips.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "acts")
data class ActEntity(
    @PrimaryKey val id: Int,
    val displayName: String
)
