package com.hsystudio.valtips.data.repository

import com.hsystudio.valtips.data.local.AgentDao
import com.hsystudio.valtips.data.local.AgentEntity
import com.hsystudio.valtips.data.remote.ValorantApi
import com.hsystudio.valtips.domain.model.Agent
import com.hsystudio.valtips.domain.repository.ValorantRepository
import com.hsystudio.valtips.domain.repository.toDomain
import com.hsystudio.valtips.domain.repository.toEntity
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValorantRepositoryImpl @Inject constructor(
    private val api: ValorantApi,
    private val dao: AgentDao
) : ValorantRepository {
    // API 호출 및 캐싱
    private suspend fun refreshIfNeeded(): Result<List<AgentEntity>> =
        try {
            val cached = dao.getAgents()
            val now = System.currentTimeMillis()
            val expired = (now - (cached.firstOrNull()?.lastUpdated ?: 0L)) > TimeUnit.DAYS.toMillis(1)

            if (cached.isEmpty() || expired) {
                val response = api.getAgents()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val entities = body.data.map { it.toEntity(now) }
                        dao.clear()
                        dao.insertAll(entities)
                        Result.success(entities)
                    } else {
                        Result.failure(Exception("통신 응답이 비어있습니다."))
                    }
                } else {
                    Result.failure(Exception("통신 실패: ${response.code()}"))
                }
            } else {
                Result.success(cached)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    // 요원 정보 조회
    override suspend fun getAgents(): Result<List<Agent>> =
        refreshIfNeeded().map { entities ->
            entities.map { it.toDomain() }
        }

    // 요원 fullPortrait URL만 조회
    override suspend fun getPortraitUrls(): Result<List<String>> =
        refreshIfNeeded().map { entities ->
            entities.mapNotNull { it.fullPortrait }
        }
}
