package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.MapEntity
import com.hsystudio.valtips.domain.model.MapListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MapDao {
    // 초기 리소스 저장
    @Upsert
    suspend fun upsert(items: List<MapEntity>)

    // 초기 리소스 초기화
    @Query("DELETE FROM maps")
    suspend fun clearAll()

    // 맵 & 맵 선택 화면 - 맵 전체 조회(맵 카드)
    @Query(
        """
    SELECT
        uuid,
        displayName,
        englishName,
        listViewIconLocal AS listImageLocal,
        IFNULL(isActiveInRotation, 0) AS isActiveInRotation
    FROM maps
    """
    )
    fun observeAllMapCards(): Flow<List<MapListItem>>

    // 맵 상세 화면 - 맵 상세 정보 조회
    @Query("SELECT * FROM maps WHERE uuid = :uuid LIMIT 1")
    fun observeByUuid(uuid: String): Flow<MapEntity?>
}
