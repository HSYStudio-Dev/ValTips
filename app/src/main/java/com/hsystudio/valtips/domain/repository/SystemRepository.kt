package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.data.remote.dto.SystemStatusDto

interface SystemRepository {
    // 시스템 상태 조회
    suspend fun getSystemStatus(): Result<SystemStatusDto>
}
