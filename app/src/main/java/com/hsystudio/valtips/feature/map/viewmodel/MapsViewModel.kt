package com.hsystudio.valtips.feature.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.domain.repository.MapRepository
import com.hsystudio.valtips.feature.map.model.MapsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    repo: MapRepository
) : ViewModel() {
    // 최종 UI 상태
    val uiState: StateFlow<MapsUiState> = combine(
        repo.observeCurrentActName(),
        repo.observeMapCards()
    ) { actName, maps ->
        // 맵 활성/제외로 데이터 분리
        val (active, retired) = maps.partition { it.isActiveInRotation }

        // 데이터가 아예 없는 경우
        val isDataEmpty = maps.isEmpty()

        // 맵 이미지 검사
        val hasCorruptedMap = maps.any { it.listImageLocal.isNullOrBlank() }

        if (isDataEmpty || hasCorruptedMap) {
            MapsUiState(
                isLoading = false,
                error = "맵 리소스를 불러오지 못했습니다.\n문제가 계속 된다면 설정 탭에서\n최신 리소스 다운로드를 진행해 주세요."
            )
        } else {
            MapsUiState(
                isLoading = false,
                actTitle = actName?.let { "$it 맵" },
                activeMaps = active,
                retiredMaps = retired,
                error = null
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MapsUiState(isLoading = true)
    )
}
