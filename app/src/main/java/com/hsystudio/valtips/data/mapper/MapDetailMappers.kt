package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.data.local.entity.MapEntity
import com.hsystudio.valtips.feature.map.model.MapDetailUiState
import com.hsystudio.valtips.feature.map.model.MapRecommendedAgentItem

// MapEntity + AgentEntity → UI 표시용 모델로 변환
fun MapEntity.toDetailUi(
    agents: List<MapRecommendedAgentItem>
): MapDetailUiState {
    // 추천 요원 uuid 리스트 변환
    val recommendedIds = listOfNotNull(
        recommendedAgent1Id,
        recommendedAgent2Id,
        recommendedAgent3Id,
        recommendedAgent4Id,
        recommendedAgent5Id
    )

    // 요원 Entity → 추천 요원 카드용 모델 변환
    val recommendedAgents = agents
        .filter { it.uuid in recommendedIds }
        .sortedBy { recommendedIds.indexOf(it.uuid) }

    return MapDetailUiState(
        uuid = uuid,
        displayName = displayName,
        englishName = englishName,
        tacticalDescription = tacticalDescription,
        splashLocal = splashLocal,
        miniMapAttackerLocal = displayIconAttackerLocal,
        miniMapDefenderLocal = displayIconDefenderLocal,
        miniMapAttackerSmokeLocal = displayIconAttackerSmokeLocal,
        miniMapDefenderSmokeLocal = displayIconDefenderSmokeLocal,
        recommendedAgents = recommendedAgents,
        hasAttackerView = !displayIconAttackerLocal.isNullOrBlank(),
        hasDefenderView = !displayIconDefenderLocal.isNullOrBlank(),
        hasAttackerSmoke = !displayIconAttackerSmokeLocal.isNullOrBlank(),
        hasDefenderSmoke = !displayIconDefenderSmokeLocal.isNullOrBlank()
    )
}
