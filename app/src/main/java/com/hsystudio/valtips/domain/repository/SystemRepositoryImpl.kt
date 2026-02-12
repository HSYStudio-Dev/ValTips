package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.data.remote.api.SystemApi
import com.hsystudio.valtips.data.remote.dto.SystemStatusDto
import javax.inject.Inject

class SystemRepositoryImpl @Inject constructor(
    private val api: SystemApi
) : SystemRepository {
    // 시스템 상태 조회
    override suspend fun getSystemStatus(): Result<SystemStatusDto> =
        runCatching {
            api.getSystemStatus()
        }
}
