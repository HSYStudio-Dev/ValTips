package com.hsystudio.valtips.util

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

// 현재 화면의 라이프사이클이 RESUMED일 때만 이동 허용 - 중복 스택 방지
fun NavController.navigateSafe(route: String) {
    val isResumed = currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED
    if (!isResumed) return

    navigate(route) {
        launchSingleTop = true
    }
}
