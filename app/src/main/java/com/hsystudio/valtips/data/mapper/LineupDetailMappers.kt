package com.hsystudio.valtips.data.mapper

import android.util.Log
import com.hsystudio.valtips.data.remote.dto.LineupDetailDto
import com.hsystudio.valtips.data.remote.dto.LineupStepDto
import com.hsystudio.valtips.feature.lineup.model.LineupDetailItem
import com.hsystudio.valtips.feature.lineup.model.LineupSideFilter
import com.hsystudio.valtips.feature.lineup.model.LineupStepItem
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// 공격/수비 필터링
private fun String.toSideFilter(): LineupSideFilter =
    when (uppercase()) {
        "ATTACKER" -> LineupSideFilter.ATTACK
        "DEFENDER" -> LineupSideFilter.DEFENSE
        else -> LineupSideFilter.ALL
    }

// 사이트 라벨링
private fun String.toSiteLabel(): String =
    when (uppercase()) {
        "A" -> "A 사이트"
        "B" -> "B 사이트"
        "C" -> "C 사이트"
        "MID" -> "미드"
        else -> this
    }

// 날짜 포멧팅
private fun String.toKoreanDate(
    pattern: String = "yyyy.MM.dd"
): String =
    try {
        val instant = Instant.parse(this)
        val kstTime = ZonedDateTime.ofInstant(
            instant,
            ZoneId.of("Asia/Seoul")
        )
        kstTime.format(DateTimeFormatter.ofPattern(pattern))
    } catch (e: Exception) {
        Log.e("LineupDetailMappers", "날짜 포멧팅 오류: $this")
        this
    }

// step_number 기준 정렬
private fun List<LineupStepDto>.toStepItems(): List<LineupStepItem> =
    this.sortedBy { it.stepNumber }.map { dto ->
        LineupStepItem(
            stepNumber = dto.stepNumber,
            description = dto.description,
            imageUrl = dto.imageUrl
        )
    }

// /lineups/{id} 응답 → 상세 UI 모델
fun LineupDetailDto.toUi(): LineupDetailItem =
    LineupDetailItem(
        id = id,
        title = title,
        description = description,
        side = side.toSideFilter(),
        site = site.toSiteLabel(),
        writer = writer,
        updatedAt = updatedAt.toKoreanDate(),
        steps = steps.toStepItems()
    )
