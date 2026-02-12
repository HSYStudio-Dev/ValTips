package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.GameModeEntity

@Dao
interface GameModeDao {
    // 초기 리소스 저장
    @Upsert
    suspend fun upsert(items: List<GameModeEntity>)

    // 초기 리소스 초기화
    @Query("DELETE FROM game_modes")
    suspend fun clearAll()
}
