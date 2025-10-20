package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.AbilityEntity

@Dao
interface AbilityDao {
    @Upsert
    suspend fun upsert(items: List<AbilityEntity>)

    @Query("SELECT * FROM abilities WHERE agentUuid = :agentUuid")
    suspend fun getByAgent(agentUuid: String): List<AbilityEntity>

    @Query("DELETE FROM abilities WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Query("DELETE FROM abilities WHERE agentUuid IN (:agentUuids)")
    suspend fun deleteByAgentUuids(agentUuids: List<String>)

    @Query("DELETE FROM abilities")
    suspend fun clearAll()
}
