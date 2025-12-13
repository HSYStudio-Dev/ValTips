package com.hsystudio.valtips.data.remote.api

import com.hsystudio.valtips.data.remote.dto.LineupListResponseDto
import com.hsystudio.valtips.data.remote.dto.LineupStatusDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LineupApi {
    // 요원 기준 맵별 라인업 가능 상태 조회
    @GET("maps/lineup-status")
    suspend fun getMapLineupStatus(
        @Query("agent_uuid") agentUuid: String
    ): List<LineupStatusDto>

    // 맵 기준 요원별 라인업 가능 상태 조회
    @GET("agents/lineup-status")
    suspend fun getAgentLineupStatus(
        @Query("map_uuid") mapUuid: String
    ): List<LineupStatusDto>

    // 요원 + 맵 기준 전체 라인업 리스트 조회
    @GET("lineups")
    suspend fun getLineups(
        @Query("agent_uuid") agentUuid: String,
        @Query("map_uuid") mapUuid: String
    ): LineupListResponseDto

    // 타임스탬프 이후 추가된 라인업 리스트 조회
    @GET("lineups/updates")
    suspend fun getLineupUpdates(
        @Query("agent_uuid") agentUuid: String,
        @Query("map_uuid") mapUuid: String,
        @Query("since") since: String
    ): LineupListResponseDto
}
