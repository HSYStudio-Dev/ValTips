package com.hsystudio.valtips.feature.lineup.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.local.dao.TierDao
import com.hsystudio.valtips.domain.repository.AgentRepository
import com.hsystudio.valtips.domain.repository.LineupRepository
import com.hsystudio.valtips.domain.repository.MapRepository
import com.hsystudio.valtips.feature.agent.model.AbilityItem
import com.hsystudio.valtips.feature.lineup.model.LineupCardItem
import com.hsystudio.valtips.feature.lineup.model.LineupsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// 콘텐츠 코어 상태(라인업 목록 + 스킬 목록 + 선택된 슬롯)
private data class LineupsCoreContent(
    val lineups: List<LineupCardItem>,
    val abilities: List<AbilityItem>,
    val selectedAbilitySlot: String?
)

// 해더 코어 상태(요원/맵/플레이스홀더 아이콘)
private data class LineupsCoreHeader(
    val agentIconLocal: String?,
    val mapSplashLocal: String?,
    val placeholderIconLocal: String?
)

@HiltViewModel
class LineupsViewModel @Inject constructor(
    private val lineupRepository: LineupRepository,
    private val mapRepository: MapRepository,
    private val agentRepository: AgentRepository,
    private val tierDao: TierDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // NavGraph에서 전달된 맵/요원 UUID
    private val mapUuid: String = checkNotNull(savedStateHandle["mapUuid"])
    private val agentUuid: String = checkNotNull(savedStateHandle["agentUuid"])

    // 라인업 리스트 상태 / 로딩 / 에러
    private val lineupsState = MutableStateFlow<List<LineupCardItem>>(emptyList())
    private val isLoadingState = MutableStateFlow(true)
    private val errorState = MutableStateFlow<String?>(null)

    // 요원 정보 구독(스킬)
    private val abilitiesFlow =
        agentRepository.observeAgentDetail(agentUuid)
            .map { detail -> detail.abilities }
            .catch { e ->
                Log.e("LineupsViewModel", "agentDetail observe 실패: ${e.message}")
                errorState.value = "요원 정보를 불러오지 못했습니다."
                emit(emptyList())
            }

    // 선택된 스킬 슬롯
    private val selectedAbilitySlotState = MutableStateFlow<String?>(null)

    // 선택된 요원 / 맵 / 플레이스홀더 아이콘
    private val agentIconFlow = agentRepository.observeAgentIconLocal(agentUuid)
    private val mapSplashFlow = mapRepository.observeMapSplashLocal(mapUuid)
    private val placeholderIconFlow = tierDao.observeTierZeroIconLocal()

    // 라인업 + 선택 슬롯 + 요원 스킬을 합친 Flow
    private val contentCoreFlow = combine(
        lineupsState,
        selectedAbilitySlotState,
        abilitiesFlow
    ) { lineups, selectedSlot, abilities ->
        // Passive 스킬 제외
        val abilitiesWithoutPassive = abilities
            .filterNot { it.slot.equals("Passive", ignoreCase = true) }

        // 선택 슬롯 기준 라인업 필터링
        val filteredLineups = if (selectedSlot.isNullOrBlank()) {
            lineups
        } else {
            lineups.filter { it.abilitySlot == selectedSlot }
        }

        LineupsCoreContent(
            lineups = filteredLineups,
            abilities = abilitiesWithoutPassive,
            selectedAbilitySlot = selectedSlot
        )
    }

    // 해더에 필요한 이미지 3개를 합친 Flow
    private val headerCoreFlow = combine(
        agentIconFlow,
        mapSplashFlow,
        placeholderIconFlow
    ) { agentIconLocal, mapSplashLocal, placeholderIconLocal ->
        LineupsCoreHeader(
            agentIconLocal = agentIconLocal,
            mapSplashLocal = mapSplashLocal,
            placeholderIconLocal = placeholderIconLocal
        )
    }

    // 콘텐츠 코어 + 헤더 코어 상태를 하나의 Flow로 합친 변수
    private val coreStateFlow = combine(
        contentCoreFlow,
        headerCoreFlow
    ) { content, header ->
        LineupsUiState(
            lineups = content.lineups,
            abilities = content.abilities,
            selectedAbilitySlot = content.selectedAbilitySlot,
            agentIconLocal = header.agentIconLocal,
            mapSplashLocal = header.mapSplashLocal,
            placeholderIconLocal = header.placeholderIconLocal
        )
    }

    // 로딩&에러 상태를 합친 최종 UI 상태
    val uiStateFlow = combine(
        coreStateFlow,
        isLoadingState,
        errorState
    ) { core, isLoading, error ->
        core.copy(
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LineupsUiState()
    )

    // 진입 시 1회 불러오기
    init {
        getLineups()
    }

    // mapUuid + agentUuid 조합의 라인업 목록을 조회
    fun getLineups() {
        viewModelScope.launch {
            isLoadingState.value = true
            errorState.value = null

            lineupRepository
                .getLineups(agentUuid = agentUuid, mapUuid = mapUuid)
                .onSuccess { list ->
                    lineupsState.value = list
                }
                .onFailure { e ->
                    Log.e("LineupsViewModel", "라인업 조회 실패 : ${e.message}")
                    errorState.value = "라인업 정보를 불러오지 못했습니다."
                }
            isLoadingState.value = false
        }
    }

    // 라인업 스킬 필터
    fun abilityFilter(slot: String) {
        selectedAbilitySlotState.value =
            if (selectedAbilitySlotState.value == slot) null else slot
    }
}
