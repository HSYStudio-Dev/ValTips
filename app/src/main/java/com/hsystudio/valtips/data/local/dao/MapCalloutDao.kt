package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.MapCalloutEntity

@Dao
interface MapCalloutDao {
    @Upsert
    suspend fun upsert(items: List<MapCalloutEntity>)

    @Query("DELETE FROM map_callouts")
    suspend fun clearAll()

    @Query("DELETE FROM map_callouts WHERE mapUuid = :mapUuid")
    suspend fun clearByMap(mapUuid: String)
}
