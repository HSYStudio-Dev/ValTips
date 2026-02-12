package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.local.entity.ActEntity
import com.hsystudio.valtips.data.remote.dto.CurrentActDto
import com.hsystudio.valtips.domain.model.ActInfo

fun CurrentActDto.toEntity(): ActEntity =
    ActEntity(
        id = id,
        displayName = displayName
    )

fun ActEntity.toDomain(): ActInfo =
    ActInfo(
        id = id,
        displayName = displayName
    )
