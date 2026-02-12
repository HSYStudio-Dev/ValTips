package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.TierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TierDao {
    // 초기 리소스 저장
    @Upsert
    suspend fun upsert(items: List<TierEntity>)

    // 초기 리소스 초기화
    @Query("DELETE FROM tiers")
    suspend fun clearAll()

    // 요원&맵 선택 / 라인업 화면 - 언랭크 이미지 조회
    @Query("SELECT largeIconLocal FROM tiers WHERE tier = 0 LIMIT 1")
    fun observeTierZeroIconLocal(): Flow<String?>
}
