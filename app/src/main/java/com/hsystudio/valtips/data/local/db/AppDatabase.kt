package com.hsystudio.valtips.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hsystudio.valtips.data.local.dao.AbilityDao
import com.hsystudio.valtips.data.local.dao.AgentDao
import com.hsystudio.valtips.data.local.dao.MapCalloutDao
import com.hsystudio.valtips.data.local.dao.MapDao
import com.hsystudio.valtips.data.local.dao.RoleDao
import com.hsystudio.valtips.data.local.entity.AbilityEntity
import com.hsystudio.valtips.data.local.entity.AgentEntity
import com.hsystudio.valtips.data.local.entity.MapCalloutEntity
import com.hsystudio.valtips.data.local.entity.MapEntity
import com.hsystudio.valtips.data.local.entity.RoleEntity

@Database(
    entities = [
        RoleEntity::class,
        AgentEntity::class,
        AbilityEntity::class,
        MapEntity::class,
        MapCalloutEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roleDao(): RoleDao

    abstract fun agentDao(): AgentDao

    abstract fun abilityDao(): AbilityDao

    abstract fun mapDao(): MapDao

    abstract fun mapCalloutDao(): MapCalloutDao
}
