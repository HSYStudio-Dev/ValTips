package com.hsystudio.valtips.data.ad

import com.hsystudio.valtips.BuildConfig

object AdConfig {
    // 구글 제공 네이티브 광고 테스트 ID
    private const val TEST_ID = "ca-app-pub-3940256099942544/2247696110"

    // 요원 화면용
    val AgentsNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-9443621239707976/5118108344"

    // 요원 상세 화면용
    val AgentDetailNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-9443621239707976/5366713291"

    // 맵 화면용
    val MapsNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-9443621239707976/7305287982"

    // 맵 상세 화면용
    val MapDetailNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-9443621239707976/5022410000"

    // 맵 선택 화면용
    val MapSelectNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-9443621239707976/2740549958"

    // 요원 선택 화면용
    val AgentSelectNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-9443621239707976/1427468289"

    // 라인업 상세 화면용
    val LineupDetailNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-9443621239707976/9651920152"

    // 설정 화면용
    val SettingNativeId: String
        get() = if (BuildConfig.DEBUG) TEST_ID else "ca-app-pub-9443621239707976/3754248721"
}
