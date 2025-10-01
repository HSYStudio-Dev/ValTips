package com.hsystudio.valtips.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// 요원 정보 테이블
@Entity(tableName = "agents")
data class AgentEntity(
    @PrimaryKey val uuid: String,
    val displayName: String,
    val description: String,
    val displayIcon: String?,
    val fullPortrait: String?,
    val roleName: String?,
    val abilities: String, // JSON 직렬화 저장
    val lastUpdated: Long
)
