package com.hsystudio.valtips.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_modes")
data class GameModeEntity(
    @PrimaryKey val uuid: String,
    val displayName: String,
    // 원본 URL
    val displayIconUrl: String?,
    // 로컬 경로
    val displayIconLocal: String?,
    // 서버 numeric id (참고용)
    val numericId: Int
)
