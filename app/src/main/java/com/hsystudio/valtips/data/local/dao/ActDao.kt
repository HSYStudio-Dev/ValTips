package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.ActEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActDao {
    // 초기 리소스 저장
    @Upsert
    suspend fun upsert(entity: ActEntity)

    // 초기 리소스 초기화
    @Query("DELETE FROM acts")
    suspend fun clearAll()

    // 맵 & 맵 선택 화면 - 시즌 액트 조회
    @Query("SELECT * FROM acts ORDER BY id DESC LIMIT 1")
    fun observeLatest(): Flow<ActEntity?>
}
