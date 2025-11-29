package com.hsystudio.valtips.feature.lineup.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.local.dao.TierDao
import com.hsystudio.valtips.domain.repository.AgentRepository
import com.hsystudio.valtips.domain.repository.LineupRepository
import com.hsystudio.valtips.domain.repository.MapRepository
import com.hsystudio.valtips.feature.lineup.model.MapLineupStatus
import com.hsystudio.valtips.feature.lineup.model.MapSelectUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapSelectViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val agentRepository: AgentRepository,
    private val lineupRepository: LineupRepository,
    private val tierDao: TierDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // NavGraph에서 전달된 요원 UUID
    private val agentUuid: String = checkNotNull(savedStateHandle["agentUuid"])

    // 라인업 상태 / 로딩 / 에러
    private val lineupStatusFlow = MutableStateFlow<List<MapLineupStatus>>(emptyList())
    private val isLoading = MutableStateFlow(true)
    private val error = MutableStateFlow<String?>(null)

    // 선택 요원 / 플레이스홀더 아이콘
    private val selectedAgentFlow = agentRepository.observeAgentCardItem(agentUuid)
    private val placeholderIconFlow = tierDao.observeTierZeroIconLocal()

    // 핵심 데이터를 하나의 Flow로 합친 변수
    private val coreStateFlow = combine(
        mapRepository.observeCurrentActName(),
        mapRepository.observeMapCards(),
        lineupStatusFlow,
        selectedAgentFlow,
        placeholderIconFlow
    ) { actName, maps, lineupStatus, agentCard, placeholderIcon ->
        val (active, retired) = maps.partition { it.isActiveInRotation }

        MapSelectUiState(
            actTitle = actName?.let { "$it 맵" },
            activeMaps = active,
            retiredMaps = retired,
            lineupStatus = lineupStatus.associate { it.uuid to it.hasLineups },
            selectedAgent = agentCard,
            placeholderIconLocal = placeholderIcon
        )
    }

    // 로딩&에러 상태를 합친 최종 UI 상태
    val uiStateFlow = combine(
        coreStateFlow,
        isLoading,
        error
    ) { core, isLoading, error ->
        core.copy(
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MapSelectUiState()
    )

    // 진입 시 1회 불러오기
    init {
        getMapLineupStatus()
    }

    // 요원 기준 맵별 라인업 상태 불러오기
    fun getMapLineupStatus() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            lineupRepository.getMapsLineupStatus(agentUuid)
                .onSuccess { list ->
                    lineupStatusFlow.value = list
                }
                .onFailure { e ->
                    Log.e("MapSelectViewModel", "요원 기준 라인업 조회 실패 : ${e.message}")
                    error.value = "라인업 정보를 불러오지 못했습니다."
                }
            isLoading.value = false
        }
    }
}
