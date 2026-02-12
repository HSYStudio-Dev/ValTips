package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.local.entity.MapEntity
import com.hsystudio.valtips.data.remote.dto.MapDto

fun MapDto.toEntity(
    localListIconPath: String? = null,
    localSplashPath: String? = null,
    localAttackerPath: String? = null,
    localDefenderPath: String? = null,
    localAttackerSmokePath: String? = null,
    localDefenderSmokePath: String? = null
): MapEntity = MapEntity(
    uuid = uuid,
    displayName = displayName,
    englishName = englishName,
    tacticalDescription = tacticalDescription,
    listViewIconUrl = listViewIcon,
    splashUrl = splash,
    displayIconAttackerUrl = displayIconAttacker,
    displayIconDefenderUrl = displayIconDefender,
    displayIconAttackerSmokeUrl = displayIconAttackerSmoke,
    displayIconDefenderSmokeUrl = displayIconDefenderSmoke,
    listViewIconLocal = localListIconPath,
    splashLocal = localSplashPath,
    displayIconAttackerLocal = localAttackerPath,
    displayIconDefenderLocal = localDefenderPath,
    displayIconAttackerSmokeLocal = localAttackerSmokePath,
    displayIconDefenderSmokeLocal = localDefenderSmokePath,
    isActiveInRotation = isActiveInRotation,
    numericId = id,
    recommendedAgent1Id = recAgent1Id,
    recommendedAgent2Id = recAgent2Id,
    recommendedAgent3Id = recAgent3Id,
    recommendedAgent4Id = recAgent4Id,
    recommendedAgent5Id = recAgent5Id
)
