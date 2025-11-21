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
    val listViewIconUrl: String?,
    val splashUrl: String?,
    val displayIconAttackerUrl: String?,
    val displayIconDefenderUrl: String?,
    val displayIconAttackerSmokeUrl: String?,
    val displayIconDefenderSmokeUrl: String?,
    // 로컬 저장 경로
    val listViewIconLocal: String?,
    val splashLocal: String?,
    val displayIconAttackerLocal: String?,
    val displayIconDefenderLocal: String?,
    val displayIconAttackerSmokeLocal: String?,
    val displayIconDefenderSmokeLocal: String?,
    // 기타 속성
    val isActiveInRotation: Boolean?,
    val numericId: Int,
    val recommendedAgent1Id: String? = null,
    val recommendedAgent2Id: String? = null,
    val recommendedAgent3Id: String? = null,
    val recommendedAgent4Id: String? = null,
    val recommendedAgent5Id: String? = null
)
