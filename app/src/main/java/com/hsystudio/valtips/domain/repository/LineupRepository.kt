package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.feature.lineup.model.LineupCardItem
import com.hsystudio.valtips.feature.lineup.model.LineupDetailItem
import com.hsystudio.valtips.feature.lineup.model.LineupStatus

interface LineupRepository {
    // 특정 요원의 맵별 라인업 존재 여부 조회
    suspend fun getMapsLineupStatus(agentUuid: String): Result<List<LineupStatus>>

    // 특정 맵의 요원별 라인업 존재 여부 조회
    suspend fun getAgentsLineupStatus(mapUuid: String): Result<List<LineupStatus>>

    // 요원 + 맵 조합에 대한 라인업 리스트 조회
    suspend fun getLineups(agentUuid: String, mapUuid: String): Result<List<LineupCardItem>>

    // 라인업 상세 정보 조회
    suspend fun getLineupDetail(lineupId: Int): Result<LineupDetailItem>
}
