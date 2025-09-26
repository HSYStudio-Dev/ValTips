package com.hsystudio.valtips.feature.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.local.UserInfoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userInfoManager: UserInfoManager
) : ViewModel() {
    // 온보딩 완료 여부
    val onboardingCompleted: StateFlow<Boolean> =
        userInfoManager
            .onboardingCompletedFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false
            )

    // 온보딩 완료 여부 저장
    fun setOnboardingCompleted() {
        viewModelScope.launch {
            userInfoManager.setOnboardingCompleted(true)
        }
    }
}
