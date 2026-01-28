package com.hsystudio.valtips.feature.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.ad.AdConfig
import com.hsystudio.valtips.data.ad.AdRepository
import com.hsystudio.valtips.data.local.AppPrefsManager
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.domain.repository.MapRepository
import com.hsystudio.valtips.feature.map.model.MapsUiState
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
class MapsViewModel @Inject constructor(
    private val repo: MapRepository,
    private val appPrefsManager: AppPrefsManager,
    private val adRepository: AdRepository
) : ViewModel() {
    // 광고 상태 관리
    private val _nativeAdState = MutableStateFlow<NativeAdUiState>(NativeAdUiState.Loading)
    val nativeAdState: StateFlow<NativeAdUiState> = _nativeAdState.asStateFlow()

    // 최종 UI 상태
    val uiState: StateFlow<MapsUiState> = combine(
        repo.observeCurrentActName(),
        repo.observeMapCards(),
        appPrefsManager.isProMemberFlow
    ) { actName, maps, isPro ->
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
                error = null,
                isProMember = isPro
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MapsUiState(isLoading = true)
    )

    init {
        observeMembership()
    }

    // AppPrefsManager의 멤버십 상태 구독
    private fun observeMembership() {
        viewModelScope.launch {
            appPrefsManager.isProMemberFlow.collectLatest { isPro ->
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
            adUnitId = AdConfig.MapsNativeId,
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
