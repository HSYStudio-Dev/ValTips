package com.hsystudio.valtips.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maps")
data class MapEntity(
    @PrimaryKey val uuid: String,
    val displayName: String,
    val englishName: String?,
    val tacticalDescription: String?,
    // 원본 URL
    val displayIconUrl: String?,
    val listViewIconUrl: String?,
    val splashUrl: String?,
    // 로컬 저장 경로
    val displayIconLocal: String?,
    val listViewIconLocal: String?,
    val splashLocal: String?,
    // 기타 속성
    val isActiveInRotation: Boolean?,
    val xMultiplier: Double?,
    val yMultiplier: Double?,
    val xScalarToAdd: Double?,
    val yScalarToAdd: Double?,
    val recommendedAgent1Uuid: String? = null,
    val recommendedAgent2Uuid: String? = null,
    val recommendedAgent3Uuid: String? = null,
    val recommendedAgent4Uuid: String? = null,
    val recommendedAgent5Uuid: String? = null,
    val numericId: Int
)
