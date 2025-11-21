package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.ActEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActDao {
    @Upsert
    suspend fun upsert(entity: ActEntity)

    @Query("SELECT * FROM acts ORDER BY id DESC LIMIT 1")
    suspend fun getLatest(): ActEntity?

    @Query("DELETE FROM acts")
    suspend fun clearAll()

    @Query("SELECT * FROM acts ORDER BY id DESC LIMIT 1")
    fun observeLatest(): Flow<ActEntity?>
}
