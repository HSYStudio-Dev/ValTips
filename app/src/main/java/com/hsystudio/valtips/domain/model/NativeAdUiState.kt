package com.hsystudio.valtips.domain.model

import com.google.android.gms.ads.nativead.NativeAd

sealed interface NativeAdUiState {
    // 로딩 상태
    data object Loading : NativeAdUiState

    // 로드 성공 상태
    data class Success(
        val ad: NativeAd
    ) : NativeAdUiState

    // 로드 실패 상태
    data object Error : NativeAdUiState
}
