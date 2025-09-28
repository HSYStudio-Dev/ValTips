package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.data.local.AgentEntity
import com.hsystudio.valtips.data.remote.AgentDto
import com.hsystudio.valtips.domain.model.Agent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun AgentDto.toEntity(now: Long): AgentEntity =
    AgentEntity(
        uuid = uuid,
        displayName = displayName,
        description = description,
        displayIcon = displayIcon,
        fullPortrait = fullPortrait,
        roleName = role?.displayName,
        abilities = Json.encodeToString(abilities ?: emptyList()),
        lastUpdated = now
    )

fun AgentEntity.toDomain(): Agent =
    Agent(
        uuid,
        displayName,
        description,
        displayIcon,
        fullPortrait,
        roleName,
        Json.decodeFromString(abilities)
    )
