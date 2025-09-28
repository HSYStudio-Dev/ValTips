package com.hsystudio.valtips.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents")
    suspend fun getAgents(): List<AgentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(agents: List<AgentEntity>)

    @Query("DELETE FROM agents")
    suspend fun clear()
}
