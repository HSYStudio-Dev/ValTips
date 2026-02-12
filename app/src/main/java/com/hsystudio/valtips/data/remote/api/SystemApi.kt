package com.hsystudio.valtips.data.remote.api

import com.hsystudio.valtips.data.remote.dto.SystemStatusDto
import retrofit2.http.GET

interface SystemApi {
    // 시스템 상태 조회
    @GET("system/status")
    suspend fun getSystemStatus(): SystemStatusDto
}
