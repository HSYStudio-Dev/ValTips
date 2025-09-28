package com.hsystudio.valtips.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// 요원 정보 DB
@Database(
    entities = [AgentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agentDao(): AgentDao
}
