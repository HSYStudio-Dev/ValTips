package com.hsystudio.valtips.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ValorantApi {
    @GET("v1/agents")
    suspend fun getAgents(
        @Query("language") language: String = "ko-KR"
    ): Response<AgentResponse>
}
