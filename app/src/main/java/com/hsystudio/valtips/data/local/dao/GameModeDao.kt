package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.GameModeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameModeDao {
    @Upsert
    suspend fun upsert(items: List<GameModeEntity>)

    @Query("SELECT * FROM game_modes ORDER BY displayName ASC")
    suspend fun getAll(): List<GameModeEntity>

    @Query("DELETE FROM game_modes")
    suspend fun clearAll()

    @Query("SELECT * FROM game_modes ORDER BY displayName ASC")
    fun observeAll(): Flow<List<GameModeEntity>>
}
