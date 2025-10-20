package com.hsystudio.valtips.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hsystudio.valtips.data.local.dao.AbilityDao
import com.hsystudio.valtips.data.local.dao.AgentDao
import com.hsystudio.valtips.data.local.dao.GameModeDao
import com.hsystudio.valtips.data.local.dao.MapCalloutDao
import com.hsystudio.valtips.data.local.dao.MapDao
import com.hsystudio.valtips.data.local.dao.RoleDao
import com.hsystudio.valtips.data.local.dao.TierDao
import com.hsystudio.valtips.data.local.entity.AbilityEntity
import com.hsystudio.valtips.data.local.entity.AgentEntity
import com.hsystudio.valtips.data.local.entity.GameModeEntity
import com.hsystudio.valtips.data.local.entity.MapCalloutEntity
import com.hsystudio.valtips.data.local.entity.MapEntity
import com.hsystudio.valtips.data.local.entity.RoleEntity
import com.hsystudio.valtips.data.local.entity.TierEntity

@Database(
    entities = [
        AgentEntity::class,
        AbilityEntity::class,
        RoleEntity::class,
        MapEntity::class,
        MapCalloutEntity::class,
        TierEntity::class,
        GameModeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agentDao(): AgentDao

    abstract fun abilityDao(): AbilityDao

    abstract fun roleDao(): RoleDao

    abstract fun mapDao(): MapDao

    abstract fun mapCalloutDao(): MapCalloutDao

    abstract fun tierDao(): TierDao

    abstract fun gameModeDao(): GameModeDao
}
