package com.hsystudio.valtips.data.remote.api

import com.hsystudio.valtips.data.remote.dto.MapLineupStatusDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LineupApi {
    // 요원 기준 맵별 라인업 가능 상태 조회
    @GET("maps/lineup-status")
    suspend fun getMapLineupStatus(
        @Query("agent_uuid") agentUuid: String
    ): List<MapLineupStatusDto>
}
