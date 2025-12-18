package com.hsystudio.valtips.feature.map.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.domain.repository.MapRepository
import com.hsystudio.valtips.feature.map.model.MapDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MapDetailViewModel @Inject constructor(
    repo: MapRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // NavGraph에서 전달된 맵 UUID
    private val mapUuid: String = checkNotNull(savedStateHandle["mapUuid"])

    // 해당 맵의 상세 정보를 관찰하는 상태 플로우
    val uiStateFlow: StateFlow<MapDetailUiState?> =
        repo.observeMapDetail(mapUuid)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )
}
