package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.local.entity.GameModeEntity
import com.hsystudio.valtips.data.remote.dto.GameModeDto

fun GameModeDto.toEntity(
    localIconPath: String? = null
) = GameModeEntity(
    uuid = uuid,
    displayName = displayName,
    displayIconUrl = displayIcon,
    displayIconLocal = localIconPath,
    numericId = id
)
