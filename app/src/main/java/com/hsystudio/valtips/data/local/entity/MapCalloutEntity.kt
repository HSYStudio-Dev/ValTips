package com.hsystudio.valtips.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "map_callouts",
    foreignKeys = [
        ForeignKey(
            entity = MapEntity::class,
            parentColumns = ["uuid"],
            childColumns = ["mapUuid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["mapUuid"])]
)
data class MapCalloutEntity(
    @PrimaryKey val id: Int,
    val mapUuid: String,
    val regionName: String,
    val superRegionName: String,
    val communityName: String?,
    val x: Double,
    val y: Double
)
