package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.feature.lineup.model.MapLineupStatus

interface LineupRepository {
    // 특정 요원의 맵별 라인업 존재 여부 조회
    suspend fun getMapsLineupStatus(agentUuid: String): Result<List<MapLineupStatus>>
}
