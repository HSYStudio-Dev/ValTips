package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.remote.dto.LineupStatusDto
import com.hsystudio.valtips.feature.lineup.model.LineupStatus

// 요원/맵 기준 라인업 응답 DTO → 맵/요원 라인업 보유 상태 모델로 변환
fun LineupStatusDto.toDomain(): LineupStatus =
    LineupStatus(
        uuid = uuid,
        hasLineups = hasLineups
    )
