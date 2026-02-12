package com.hsystudio.valtips.feature.lineup.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.ad.AdConfig
import com.hsystudio.valtips.data.ad.AdRepository
import com.hsystudio.valtips.data.local.AppPrefsManager
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.domain.repository.LineupRepository
import com.hsystudio.valtips.feature.lineup.model.LineupDetailItem
import com.hsystudio.valtips.feature.lineup.model.LineupDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LineupDetailViewModel @Inject constructor(
    private val lineupRepository: LineupRepository,
    private val appPrefsManager: AppPrefsManager,
    private val adRepository: AdRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // NavGraph에서 전달된 lineupId
    private val lineupId: Int = checkNotNull(savedStateHandle["lineupId"])

    // 상세 정보 상태 / 로딩 / 에러
    private val detailState = MutableStateFlow<LineupDetailItem?>(null)
    private val isLoadingState = MutableStateFlow(true)
    private val errorState = MutableStateFlow<String?>(null)

    // 광고 상태 관리
    private val _nativeAdState = MutableStateFlow<NativeAdUiState>(NativeAdUiState.Loading)
    val nativeAdState: StateFlow<NativeAdUiState> = _nativeAdState.asStateFlow()

    // 프로 멤버십 상태
    private val _isProMember = MutableStateFlow(false)
    val isProMember: StateFlow<Boolean> = _isProMember.asStateFlow()

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
        observeMembership()
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

    // AppPrefsManager의 멤버십 상태 구독
    private fun observeMembership() {
        viewModelScope.launch {
            appPrefsManager.isProMemberFlow.collectLatest { isPro ->
                _isProMember.value = isPro
                if (isPro) {
                    destroyAd()
                } else {
                    loadAd()
                }
            }
        }
    }

    // 광고 로드 함수
    private fun loadAd() {
        if (_nativeAdState.value is NativeAdUiState.Success) return

        _nativeAdState.value = NativeAdUiState.Loading

        adRepository.loadNativeAd(
            adUnitId = AdConfig.LineupDetailNativeId,
            onLoaded = { ad ->
                destroyAd()
                _nativeAdState.value = NativeAdUiState.Success(ad)
            },
            onFailed = {
                _nativeAdState.value = NativeAdUiState.Error
            }
        )
    }

    // 광고 메모리 해제 함수
    private fun destroyAd() {
        val currentState = _nativeAdState.value
        if (currentState is NativeAdUiState.Success) {
            currentState.ad.destroy()
        }
        _nativeAdState.value = NativeAdUiState.Loading
    }

    // ViewModel이 사라질 때 광고 객체 정리
    override fun onCleared() {
        super.onCleared()
        destroyAd()
    }
}
