package com.hsystudio.valtips.feature.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.domain.model.MapsUiState
import com.hsystudio.valtips.domain.repository.MapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    repo: MapRepository
) : ViewModel() {
    // 맵을 활성/제외로 분리하여 UI 상태를 실시간 제공하는 플로우
    val uiStateFlow = combine(
        repo.observeCurrentActName(),
        repo.observeMapCards()
    ) { actName, maps ->
        val (active, retired) = maps.partition { it.isActiveInRotation }
        MapsUiState(
            actTitle = actName?.let { "$it 맵" },
            activeMaps = active,
            retiredMaps = retired
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MapsUiState()
    )
}
