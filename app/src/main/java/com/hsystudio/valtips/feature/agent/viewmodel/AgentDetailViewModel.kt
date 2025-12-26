package com.hsystudio.valtips.feature.agent.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.domain.repository.AgentRepository
import com.hsystudio.valtips.feature.agent.model.AgentDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AgentDetailViewModel @Inject constructor(
    repo: AgentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // NavGraph에서 전달된 요원 UUID
    private val agentUuid: String = checkNotNull(savedStateHandle["agentUuid"])

    // 해당 요원의 상세 정보를 관찰하는 상태 플로우
    val uiState: StateFlow<AgentDetailUiState?> =
        repo.observeAgentDetail(agentUuid)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )
}
