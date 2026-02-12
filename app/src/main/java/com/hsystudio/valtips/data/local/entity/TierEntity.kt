package com.hsystudio.valtips.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tiers")
data class TierEntity(
    @PrimaryKey val tier: Int,
    val tierName: String,
    val color: String?,
    // 원본 URL
    val largeIconUrl: String?,
    // 로컬 경로
    val largeIconLocal: String?,
    // 서버 numeric id (참고용)
    val numericId: Int
)
