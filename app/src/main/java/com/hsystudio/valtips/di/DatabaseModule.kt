package com.hsystudio.valtips.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hsystudio.valtips.data.local.dao.AbilityDao
import com.hsystudio.valtips.data.local.dao.AgentDao
import com.hsystudio.valtips.data.local.dao.MapCalloutDao
import com.hsystudio.valtips.data.local.dao.MapDao
import com.hsystudio.valtips.data.local.dao.RoleDao
import com.hsystudio.valtips.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DB_NAME = "valtips.db"

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DB_NAME
    )
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .build()

    @Provides
    fun provideAgentDao(db: AppDatabase): AgentDao = db.agentDao()

    @Provides
    fun provideRoleDao(db: AppDatabase): RoleDao = db.roleDao()

    @Provides
    fun provideAbilityDao(db: AppDatabase): AbilityDao = db.abilityDao()

    @Provides
    fun provideMapDao(db: AppDatabase): MapDao = db.mapDao()

    @Provides
    fun provideMapCalloutDao(db: AppDatabase): MapCalloutDao = db.mapCalloutDao()
}
