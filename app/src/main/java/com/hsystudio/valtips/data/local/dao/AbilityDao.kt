package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.AbilityEntity

@Dao
interface AbilityDao {
    // 초기 리소스 저장
    @Upsert
    suspend fun upsert(items: List<AbilityEntity>)

    // 초기 리소스 초기화
    @Query("DELETE FROM abilities")
    suspend fun clearAll()
}
