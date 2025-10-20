package com.hsystudio.valtips.data.remote.api

import com.hsystudio.valtips.data.remote.dto.AgentDto
import com.hsystudio.valtips.data.remote.dto.MapDto
import com.hsystudio.valtips.data.remote.dto.ResourceInfoDto
import com.hsystudio.valtips.data.remote.dto.SyncResponse
import com.hsystudio.valtips.data.remote.dto.TierDto
import com.hsystudio.valtips.data.remote.dto.UpdatesInfoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ResourceApi {
    // 리소스 용량 조회
    @GET("resources/info")
    suspend fun getResourceInfo(): ResourceInfoDto

    // 업데이트 내역 확인(버전 확인)
    @GET("resources/updates/info")
    suspend fun getUpdatesInfo(
        @Query("since") since: String? = null
    ): UpdatesInfoDto

    // 델타 동기화: 변경 데이터
    @GET("sync")
    suspend fun getDelta(
        @Query("since") since: String
    ): SyncResponse

    // 전체 동기화: 요원 전체
    @GET("agents/")
    suspend fun getAgents(): List<AgentDto>

    // 전체 동기화: 맵 전체
    @GET("maps/")
    suspend fun getMaps(): List<MapDto>

    // 전체 동기화: 티어 전체
    @GET("tiers/")
    suspend fun getTiers(): List<TierDto>
}
