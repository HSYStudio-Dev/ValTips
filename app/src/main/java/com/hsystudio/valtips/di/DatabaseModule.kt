package com.hsystudio.valtips.di

import android.content.Context
import androidx.room.Room
import com.hsystudio.valtips.data.local.AgentDao
import com.hsystudio.valtips.data.local.AppDatabase
import com.hsystudio.valtips.data.remote.ValorantApi
import com.hsystudio.valtips.data.repository.ValorantRepositoryImpl
import com.hsystudio.valtips.domain.repository.ValorantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room
        .databaseBuilder(
            context,
            AppDatabase::class.java,
            "valtips.db"
        ).build()

    @Provides
    fun provideAgentDao(db: AppDatabase): AgentDao = db.agentDao()

    @Provides
    @Singleton
    fun provideAgentRepository(
        api: ValorantApi,
        dao: AgentDao
    ): ValorantRepository = ValorantRepositoryImpl(api, dao)
}
