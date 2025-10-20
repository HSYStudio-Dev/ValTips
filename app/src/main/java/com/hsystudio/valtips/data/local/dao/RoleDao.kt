package com.hsystudio.valtips.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hsystudio.valtips.data.local.entity.RoleEntity

@Dao
interface RoleDao {
    @Upsert
    suspend fun upsert(items: List<RoleEntity>)

    @Query("SELECT * FROM roles")
    suspend fun getAll(): List<RoleEntity>

    @Query("DELETE FROM roles WHERE uuid IN (:uuids)")
    suspend fun deleteByUuids(uuids: List<String>)

    @Query("DELETE FROM roles")
    suspend fun clearAll()
}
