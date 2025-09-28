package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.domain.model.Agent

interface ValorantRepository {
    // 요원 정보 전체 불러오기
    suspend fun getAgents(): Result<List<Agent>>

    // 요원 fullPortrait URL만 불러오기
    suspend fun getPortraitUrls(): Result<List<String>>
}
