package com.hsystudio.valtips.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.hsystudio.valtips.data.local.entity.AbilityEntity
import com.hsystudio.valtips.data.local.entity.AgentEntity
import com.hsystudio.valtips.data.local.entity.RoleEntity

data class AgentWithDetails(
    @Embedded
    val agent: AgentEntity,

    @Relation(
        parentColumn = "roleUuid",
        entityColumn = "uuid"
    )
    val role: RoleEntity,

    @Relation(
        parentColumn = "uuid",
        entityColumn = "agentUuid"
    )
    val abilities: List<AbilityEntity>
)
