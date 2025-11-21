package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.MapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MapDao {
    @Upsert
    suspend fun upsert(items: List<MapEntity>)

    @Query("SELECT * FROM maps WHERE uuid = :uuid LIMIT 1")
    suspend fun getByUuid(uuid: String): MapEntity?

    @Query("SELECT * FROM maps")
    suspend fun getAll(): List<MapEntity>

    @Query("DELETE FROM maps")
    suspend fun clearAll()

    @Query("SELECT * FROM maps")
    fun observeMaps(): Flow<List<MapEntity>>

    @Query("SELECT * FROM maps WHERE uuid = :uuid LIMIT 1")
    fun observeByUuid(uuid: String): Flow<MapEntity?>
}
