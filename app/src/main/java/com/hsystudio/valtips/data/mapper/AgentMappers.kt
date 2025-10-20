package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.local.entity.AbilityEntity
import com.hsystudio.valtips.data.local.entity.AgentEntity
import com.hsystudio.valtips.data.local.entity.RoleEntity
import com.hsystudio.valtips.data.remote.dto.AbilityDto
import com.hsystudio.valtips.data.remote.dto.AgentDto
import com.hsystudio.valtips.data.remote.dto.RoleDto

/** Role */
fun RoleDto.toEntity(localIconPath: String? = null) = RoleEntity(
    uuid = uuid,
    displayName = displayName,
    displayIconUrl = displayIcon,
    displayIconLocal = localIconPath,
    numericId = id
)

/** Agent
 *  - roleUuid는 내부 role.uuid 사용
 *  - 이미지 로컬 경로는 파일 저장 이후 주입하거나, 여기선 null 기본
 */
fun AgentDto.toEntity(
    roleUuid: String = role.uuid,
    localIconPath: String? = null,
    localPortraitPath: String? = null
) = AgentEntity(
    uuid = uuid,
    displayName = displayName,
    originCountry = originCountry,
    description = description,
    displayIconUrl = displayIcon,
    displayIconLocal = localIconPath,
    fullPortraitUrl = fullPortrait,
    fullPortraitLocal = localPortraitPath,
    numericId = id,
    roleUuid = roleUuid
)

/** Ability
 *  - DTO에는 agentUuid가 없으므로, 상위 Agent의 uuid를 외부에서 주입
 */
fun AbilityDto.toEntity(
    agentUuid: String,
    localIconPath: String? = null
) = AbilityEntity(
    id = id,
    agentUuid = agentUuid,
    slot = slot,
    displayName = displayName,
    description = description,
    displayIconUrl = displayIcon,
    displayIconLocal = localIconPath,
    details = details
)
