package com.hsystudio.valtips.feature.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.local.UserInfoManager
import com.hsystudio.valtips.domain.repository.ValorantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userInfoManager: UserInfoManager,
    private val valorantRepository: ValorantRepository
) : ViewModel() {
    // 온보딩 완료 여부
    val onboardingCompleted: StateFlow<Boolean> =
        userInfoManager.onboardingCompletedFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false
            )

    // 요원 이미지 URL 리스트
    private val _portraitUrls = MutableStateFlow<List<String>>(emptyList())
    val portraitUrls: StateFlow<List<String>> = _portraitUrls

    // 에러 메시지 이벤트
    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    // 진입 시 요원 정보 불러오기
    init {
        loadPortraits()
    }

    // 온보딩 완료 여부 저장
    fun setOnboardingCompleted() {
        viewModelScope.launch {
            userInfoManager.setOnboardingCompleted(true)
        }
    }

    // 요원 이미지 URL 불러오기
    private fun loadPortraits() {
        viewModelScope.launch {
            val result = valorantRepository.getPortraitUrls()

            if (result.isSuccess) {
                _portraitUrls.value = result.getOrNull().orEmpty()
            } else if (result.isFailure) {
                Log.e("LoginViewModel", "loadPortraits() 실패 : ${result.exceptionOrNull()?.message}")
                val message = "네트워크 오류가 발생했습니다.\n인터넷 연결 상태를 확인하고\n앱을 다시 실행해 주세요."
                _errorEvents.emit(message)
            }
        }
    }
}
