package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.local.entity.TierEntity
import com.hsystudio.valtips.data.remote.dto.TierDto

fun TierDto.toEntity(
    localIconPath: String? = null
) = TierEntity(
    tier = tier,
    tierName = tierName,
    color = color,
    largeIconUrl = largeIcon,
    largeIconLocal = localIconPath,
    numericId = id
)
