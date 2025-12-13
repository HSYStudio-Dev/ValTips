package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.AgentEntity
import com.hsystudio.valtips.data.local.relation.AgentWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Upsert
    suspend fun upsert(items: List<AgentEntity>)

    @Query("SELECT * FROM agents")
    suspend fun getAll(): List<AgentEntity>

    @Query("DELETE FROM agents WHERE uuid IN (:uuids)")
    suspend fun deleteByUuids(uuids: List<String>)

    @Query("DELETE FROM agents")
    suspend fun clearAll()

    // --- 관계 조회 ---
    @Transaction
    @Query("SELECT * FROM agents WHERE uuid = :uuid LIMIT 1")
    suspend fun getWithDetails(uuid: String): AgentWithDetails?

    @Transaction
    @Query("SELECT * FROM agents")
    suspend fun getAllWithDetails(): List<AgentWithDetails>

    // --- Flow 버전 (UI 바인딩용) ---
    @Transaction
    @Query("SELECT * FROM agents WHERE uuid = :uuid LIMIT 1")
    fun observeWithDetails(uuid: String): Flow<AgentWithDetails?>

    @Transaction
    @Query("SELECT * FROM agents")
    fun observeAllWithDetails(): Flow<List<AgentWithDetails>>

    // --- 맵 상세 추천 요원 조회 ---
    @Query("SELECT * FROM agents")
    fun observeAll(): Flow<List<AgentEntity>>

    // 여러 agentUuid에 대해 details를 한 번에 조회
    @Transaction
    @Query("SELECT * FROM agents WHERE uuid IN (:uuids)")
    suspend fun getWithDetailsByUuids(uuids: List<String>): List<AgentWithDetails>
}
