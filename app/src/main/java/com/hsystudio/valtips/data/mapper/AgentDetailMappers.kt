package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.local.relation.AgentWithDetails
import com.hsystudio.valtips.feature.agent.model.AbilityUi
import com.hsystudio.valtips.feature.agent.model.AgentDetailUi

// 스킬 슬롯 순서 고정
private fun slotOrder(slot: String) = when (slot.lowercase()) {
    "passive"  -> -1
    "ability1" -> 0
    "ability2" -> 1
    "grenade"  -> 2
    "ultimate" -> 3
    else -> 99
}

// 스킬 슬롯 라벨 지정
private fun slotLabel(slot: String) = when (slot.lowercase()) {
    "ability1" -> "C"
    "ability2" -> "Q"
    "grenade"  -> "E"
    "ultimate" -> "X"
    else -> slot
}

// Agent + Role + Abilities → UI 표시용 모델로 변환
fun AgentWithDetails.toAgentDetailUi(): AgentDetailUi {
    val ordered = abilities
        .sortedBy { slotOrder(it.slot) }

    // AgentDetailUi로 매핑
    return AgentDetailUi(
        uuid = agent.uuid,
        name = agent.displayName,
        roleName = role.displayName,
        origin = agent.originCountry,
        description = agent.description,
        portraitLocal = agent.fullPortraitLocal,
        iconLocal = agent.displayIconLocal,
        roleIconLocal = role.displayIconLocal,
        abilities = ordered.map {
            AbilityUi(
                slot = slotLabel(it.slot),
                name = it.displayName,
                iconLocal = it.displayIconLocal,
                description = it.description,
                details = it.details
            )
        }
    )
}
