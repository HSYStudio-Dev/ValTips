package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.data.mapper.toDomain
import com.hsystudio.valtips.data.remote.api.LineupApi
import com.hsystudio.valtips.feature.lineup.model.MapLineupStatus
import javax.inject.Inject

class LineupRepositoryImpl @Inject constructor(
    private val api: LineupApi
) : LineupRepository {
    // 특정 요원의 맵별 라인업 존재 여부 조회
    override suspend fun getMapsLineupStatus(agentUuid: String): Result<List<MapLineupStatus>> =
        runCatching {
            api.getMapLineupStatus(agentUuid).map { it.toDomain() }
        }
}
