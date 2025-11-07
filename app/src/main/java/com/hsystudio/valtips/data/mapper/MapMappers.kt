package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.local.entity.MapCalloutEntity
import com.hsystudio.valtips.data.local.entity.MapEntity
import com.hsystudio.valtips.data.remote.dto.MapCalloutDto
import com.hsystudio.valtips.data.remote.dto.MapDto

fun MapDto.toEntity(
    localDisplayIconPath: String? = null,
    localListIconPath: String? = null,
    localSplashPath: String? = null
) = MapEntity(
    uuid = uuid,
    displayName = displayName,
    englishName = englishName,
    tacticalDescription = tacticalDescription,
    displayIconUrl = displayIcon,
    listViewIconUrl = listViewIcon,
    splashUrl = splash,
    displayIconLocal = localDisplayIconPath,
    listViewIconLocal = localListIconPath,
    splashLocal = localSplashPath,
    isActiveInRotation = isActiveInRotation,
    xMultiplier = xMultiplier,
    yMultiplier = yMultiplier,
    xScalarToAdd = xScalarToAdd,
    yScalarToAdd = yScalarToAdd,
    recommendedAgent1Uuid = recommendedAgent1Id,
    recommendedAgent2Uuid = recommendedAgent2Id,
    recommendedAgent3Uuid = recommendedAgent3Id,
    recommendedAgent4Uuid = recommendedAgent4Id,
    recommendedAgent5Uuid = recommendedAgent5Id,
    numericId = id
)

fun MapCalloutDto.toEntity(
    mapUuid: String
) = MapCalloutEntity(
    id = id,
    mapUuid = mapUuid,
    regionName = regionName,
    superRegionName = superRegionName,
    communityName = communityName,
    x = location.x,
    y = location.y
)
