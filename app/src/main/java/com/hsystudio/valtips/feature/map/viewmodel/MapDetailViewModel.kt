package com.hsystudio.valtips.feature.map.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.ad.AdConfig
import com.hsystudio.valtips.data.ad.AdRepository
import com.hsystudio.valtips.data.local.AppPrefsManager
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.domain.repository.MapRepository
import com.hsystudio.valtips.feature.map.model.MapDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapDetailViewModel @Inject constructor(
    private val repo: MapRepository,
    private val appPrefsManager: AppPrefsManager,
    private val adRepository: AdRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // NavGraph에서 전달된 맵 UUID
    private val mapUuid: String = checkNotNull(savedStateHandle["mapUuid"])

    // 해당 맵의 상세 정보를 관찰하는 상태 플로우
    val uiState: StateFlow<MapDetailUiState?> =
        repo.observeMapDetail(mapUuid)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    // 광고 상태 관리
    private val _nativeAdState = MutableStateFlow<NativeAdUiState>(NativeAdUiState.Loading)
    val nativeAdState: StateFlow<NativeAdUiState> = _nativeAdState.asStateFlow()

    // 프로 멤버십 상태
    private val _isProMember = MutableStateFlow(false)
    val isProMember: StateFlow<Boolean> = _isProMember.asStateFlow()

    init {
        observeMembership()
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
            adUnitId = AdConfig.MapDetailNativeId,
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
