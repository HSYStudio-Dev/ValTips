package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.AgentEntity
import com.hsystudio.valtips.data.local.relation.AgentWithDetails
import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.feature.login.model.PortraitItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    // 초기 리소스 저장
    @Upsert
    suspend fun upsert(items: List<AgentEntity>)

    // 초기 리소스 초기화
    @Query("DELETE FROM agents")
    suspend fun clearAll()

    // 로그인 화면 - 요원 초상화 조회
    @Query("SELECT fullPortraitUrl, fullPortraitLocal FROM agents")
    suspend fun getAllPortrait(): List<PortraitItem>

    // 요원 & 요원 선택 화면 - 전체 요원 조회(요원 카드)
    @Query("SELECT uuid, roleUuid, displayIconLocal AS agentIconLocal FROM agents")
    fun observeAllCards(): Flow<List<AgentCardItem>>

    // 요원 & 요원 선택 화면 - 역할별 요원 조회(요원 카드)
    @Query("SELECT uuid, roleUuid, displayIconLocal AS agentIconLocal FROM agents WHERE roleUuid = :roleUuid")
    fun observeCardsByRole(roleUuid: String): Flow<List<AgentCardItem>>

    // --- Flow 버전 (UI 바인딩용) ---
    @Transaction
    @Query("SELECT * FROM agents WHERE uuid = :uuid LIMIT 1")
    fun observeWithDetails(uuid: String): Flow<AgentWithDetails?>

    // --- 맵 상세 추천 요원 조회 ---
    @Query("SELECT * FROM agents")
    fun observeAll(): Flow<List<AgentEntity>>

    // 여러 agentUuid에 대해 details를 한 번에 조회
    @Transaction
    @Query("SELECT * FROM agents WHERE uuid IN (:uuids)")
    suspend fun getWithDetailsByUuids(uuids: List<String>): List<AgentWithDetails>
}
