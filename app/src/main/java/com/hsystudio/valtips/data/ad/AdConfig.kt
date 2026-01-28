package com.hsystudio.valtips.data.ad

import com.hsystudio.valtips.BuildConfig

object AdConfig {
    // 구글 제공 네이티브 광고 테스트 ID
    private const val TEST_ID = "ca-app-pub-3940256099942544/2247696110"

    // 요원 화면용
    val AgentsNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-3940256099942544/2247696110"

    // 맵 화면용
    val MapsNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-3940256099942544/2247696110"

    // 설정 화면용
    val SettingNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-3940256099942544/2247696110"
}
