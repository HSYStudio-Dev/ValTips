package com.hsystudio.valtips.feature.lineup.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.domain.repository.LineupRepository
import com.hsystudio.valtips.feature.lineup.model.LineupDetailItem
import com.hsystudio.valtips.feature.lineup.model.LineupDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LineupDetailViewModel @Inject constructor(
    private val lineupRepository: LineupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // NavGraph에서 전달된 lineupId
    private val lineupId: Int = checkNotNull(savedStateHandle["lineupId"])

    // 상세 정보 상태 / 로딩 / 에러
    private val detailState = MutableStateFlow<LineupDetailItem?>(null)
    private val isLoadingState = MutableStateFlow(true)
    private val errorState = MutableStateFlow<String?>(null)

    // 최종 UI 상태
    val uiStateFlow = combine(
        detailState,
        isLoadingState,
        errorState
    ) { detail, isLoading, error ->
        LineupDetailUiState(
            detail = detail,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LineupDetailUiState()
    )

    init {
        getLineupDetail()
    }

    // 라인업 상세 조회
    fun getLineupDetail() {
        viewModelScope.launch {
            isLoadingState.value = true
            errorState.value = null

            lineupRepository.getLineupDetail(lineupId)
                .onSuccess { detail ->
                    detailState.value = detail
                }
                .onFailure { e ->
                    Log.e("LineupDetailViewModel", "상세 조회 실패: ${e.message}")
                    errorState.value = "라인업 상세 정보를 불러오지 못했습니다."
                }
            isLoadingState.value = false
        }
    }
}
