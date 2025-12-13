package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.remote.dto.LineupDto
import com.hsystudio.valtips.data.remote.dto.LineupStatusDto
import com.hsystudio.valtips.feature.lineup.model.LineupCardBase
import com.hsystudio.valtips.feature.lineup.model.LineupSideFilter
import com.hsystudio.valtips.feature.lineup.model.LineupStatus

// 요원/맵 기준 라인업 응답 DTO → 맵/요원 라인업 보유 상태 모델로 변환
fun LineupStatusDto.toDomain(): LineupStatus =
    LineupStatus(
        uuid = uuid,
        hasLineups = hasLineups
    )

// /lineups 응답 DTO → 라인업 UI 모델로 변환
fun LineupDto.toBase(): LineupCardBase =
    LineupCardBase(
        id = id,
        title = title,
        writer = writer,
        side = when (side.uppercase()) {
            "ATTACKER" -> LineupSideFilter.ATTACK
            "DEFENDER" -> LineupSideFilter.DEFENSE
            else -> LineupSideFilter.ALL
        },
        thumbnail = thumbnail,
        abilitySlotUi = slotLabel(abilitySlot),
        abilitySlotRaw = abilitySlot,
        agentUuid = agentUuid
    )
