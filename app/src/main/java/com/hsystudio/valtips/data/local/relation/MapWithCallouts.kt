package com.hsystudio.valtips.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.hsystudio.valtips.data.local.entity.MapCalloutEntity
import com.hsystudio.valtips.data.local.entity.MapEntity

data class MapWithCallouts(
    @Embedded
    val map: MapEntity,

    @Relation(
        parentColumn = "uuid",
        entityColumn = "mapUuid"
    )
    val callouts: List<MapCalloutEntity>
)
