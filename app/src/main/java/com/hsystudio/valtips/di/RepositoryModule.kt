package com.hsystudio.valtips.di

import com.hsystudio.valtips.domain.repository.AgentRepository
import com.hsystudio.valtips.domain.repository.AgentRepositoryImpl
import com.hsystudio.valtips.domain.repository.LineupRepository
import com.hsystudio.valtips.domain.repository.LineupRepositoryImpl
import com.hsystudio.valtips.domain.repository.MapRepository
import com.hsystudio.valtips.domain.repository.MapRepositoryImpl
import com.hsystudio.valtips.domain.repository.SystemRepository
import com.hsystudio.valtips.domain.repository.SystemRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Repository 의존성 주입 모듈
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSystemRepository(
        impl: SystemRepositoryImpl
    ): SystemRepository

    @Binds
    @Singleton
    abstract fun bindAgentRepository(
        impl: AgentRepositoryImpl
    ): AgentRepository

    @Binds
    @Singleton
    abstract fun bindMapRepository(
        impl: MapRepositoryImpl
    ): MapRepository

    @Binds
    @Singleton
    abstract fun bindLineupRepository(
        impl: LineupRepositoryImpl
    ): LineupRepository
}
