package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.TierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TierDao {
    @Upsert
    suspend fun upsert(items: List<TierEntity>)

    @Query("SELECT * FROM tiers ORDER BY tier ASC")
    suspend fun getAll(): List<TierEntity>

    @Query("DELETE FROM tiers")
    suspend fun clearAll()

    @Query("SELECT * FROM tiers ORDER BY tier ASC")
    fun observeAll(): Flow<List<TierEntity>>
}
