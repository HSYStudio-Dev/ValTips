package com.hsystudio.valtips.feature.login.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.data.local.AppPrefsManager
import com.hsystudio.valtips.data.local.dao.AgentDao
import com.hsystudio.valtips.data.repository.ResourceRepository
import com.hsystudio.valtips.domain.model.TermsPolicy
import com.hsystudio.valtips.feature.login.model.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsManager: AppPrefsManager,
    private val repository: ResourceRepository,
    private val agentDao: AgentDao
) : ViewModel() {
    private val tag = "LoginViewModel"

    // --- 스플래시 전용 --- //
    // 동기화 상태
    private val _syncRunning = MutableStateFlow(false)
    val syncRunning: StateFlow<Boolean> = _syncRunning.asStateFlow()

    // 동기화 단계
    private val _syncPhase = MutableStateFlow<String?>(null)
    val syncPhase: StateFlow<String?> = _syncPhase.asStateFlow()

    // 동기화 진행률
    private val _progressPercent = MutableStateFlow(0)
    val progressPercent: StateFlow<Int> = _progressPercent.asStateFlow()

    // 동기화 다운로드 속도
    private val _progressLabel = MutableStateFlow<String?>(null)
    val progressLabel: StateFlow<String?> = _progressLabel.asStateFlow()

    // 동기화 후 네비게이션
    private val _destination = MutableSharedFlow<Destination>(extraBufferCapacity = 1)
    val destination: SharedFlow<Destination> = _destination.asSharedFlow()

    // --- 다운로드 다이얼로그 --- //
    // 출력 유무
    private val _downloadDialogVisible = MutableStateFlow(false)
    val downloadDialogVisible: StateFlow<Boolean> = _downloadDialogVisible.asStateFlow()

    // 리소스 용량
    private val _downloadSizeMb = MutableStateFlow<Double?>(null)
    val downloadSizeMb: StateFlow<Double?> = _downloadSizeMb.asStateFlow()

    // 네트워크 연결 상태
    private val _networkType = MutableStateFlow("알 수 없음")
    val networkType: StateFlow<String> = _networkType.asStateFlow()

    // 에러 이벤트
    private val _errorEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    // 종료 이벤트
    private val _exitApp = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val exitApp: SharedFlow<Unit> = _exitApp.asSharedFlow()

    // --- 로그인 화면 --- //
    // 이미지 리스트
    private val _portraitData = MutableStateFlow<List<String>>(emptyList())
    val portraitData: StateFlow<List<String>> = _portraitData.asStateFlow()

    // ─────────────────────────────
    // 약관/개인정보처리방침 동의(버전 관리)
    // ─────────────────────────────
    // 사용자가 마지막으로 동의한 버전(없으면 null)
    private val acceptedTermsVersion: StateFlow<String?> =
        prefsManager.acceptedTermsVersionFlow
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val acceptedPrivacyVersion: StateFlow<String?> =
        prefsManager.acceptedPrivacyVersionFlow
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // 현재 앱 버전 기준 재동의 필요 여부
    val isPolicyConsentRequired: StateFlow<Boolean> =
        combine(acceptedTermsVersion, acceptedPrivacyVersion) { terms, privacy ->
            val termsOk = (terms == TermsPolicy.REQUIRED_TERMS_VERSION)
            val privacyOk = (privacy == TermsPolicy.REQUIRED_PRIVACY_VERSION)
            !(termsOk && privacyOk)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    // 동의 여부에 따른 UI 분기용
    val isReConsent: StateFlow<Boolean> =
        combine(acceptedTermsVersion, acceptedPrivacyVersion) { terms, privacy ->
            val hasAny = !terms.isNullOrBlank() || !privacy.isNullOrBlank()
            val requiredMismatch =
                terms != TermsPolicy.REQUIRED_TERMS_VERSION || privacy != TermsPolicy.REQUIRED_PRIVACY_VERSION
            hasAny && requiredMismatch
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // ─────────────────────────────
    // 함수 정의
    // ─────────────────────────────
    // 온보딩 완료 저장
    fun setOnboardingCompleted() {
        viewModelScope.launch { prefsManager.setOnboardingCompleted(true) }
    }

    /**
     * 버전 체크 & 동기화 분기
     * - lastSync가 없으면: 용량 조회 → 동의 다이얼로그 표시
     * - lastSync가 있으면: 델타 동기화 바로 시작
     */
    fun runStartupFlow(isLoggedIn: Boolean = false) {
        if (_syncRunning.value) return

        viewModelScope.launch {
            try {
                val last = prefsManager.lastSyncFlow.firstOrNull()
                if (last.isNullOrBlank()) {
                    // 초기 실행: 용량 조회 + 동의 다이얼로그
                    _syncPhase.value = "리소스 정보 확인 중…"
                    repository.getResourceSize()
                        .onSuccess { size ->
                            _downloadSizeMb.value = size
                            _networkType.value = resolveNetworkType()
                            _downloadDialogVisible.value = true
                        }.onFailure { e ->
                            Log.e(tag, "getResourceSize() 실패", e)
                            _errorEvents.tryEmit("네트워크 연결이 불안정하여 앱이 종료됩니다.")
                            _exitApp.tryEmit(Unit)
                        }
                } else {
                    // 재실행: 델타 동기화
                    startDeltaSyncThenRoute(isLoggedIn)
                }
            } catch (e: Exception) {
                Log.e(tag, "runStartupFlow() 실패", e)
                _errorEvents.tryEmit("네트워크 오류로 인해 앱이 종료됩니다.")
                _exitApp.tryEmit(Unit)
            }
        }
    }

    /** 다이얼로그 취소 → 앱 종료 */
    fun cancelInitialDownload() {
        _downloadDialogVisible.value = false
        _errorEvents.tryEmit("필수 리소스를 다운받지 않아 앱이 종료됩니다.")
        _exitApp.tryEmit(Unit)
    }

    /** 다이얼로그 확인 → 초기 동기화 시작 */
    fun confirmInitialDownload(isLoggedIn: Boolean = false) {
        _downloadDialogVisible.value = false
        startInitialSyncThenRoute(isLoggedIn)
    }

    // 바이트 포맷
    @SuppressLint("DefaultLocale")
    private fun humanBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return String.format(Locale.US, "%.1f %s", bytes / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
    }

    /** 초기 동기화 */
    private fun startInitialSyncThenRoute(isLoggedIn: Boolean) {
        if (_syncRunning.value) return
        _syncRunning.value = true
        _progressPercent.value = 0
        _syncPhase.value = "필수 리소스 다운로드 중…"

        viewModelScope.launch {
            repository.initialSync(onProgressBytes = { done, total, read, totalBytes, bps ->
                val percent = if (totalBytes > 0) {
                    ((read.toDouble() / totalBytes.toDouble()) * 100).roundToInt()
                } else {
                    ((done.toDouble() / total.toDouble()) * 100).roundToInt()
                }.coerceIn(0, 100)
                _progressPercent.value = percent

                val leftPart = if (totalBytes > 0) {
                    "${humanBytes(read)} / ${humanBytes(totalBytes)}"
                } else {
                    "$done / $total 항목"
                }
                val speed = if (bps > 0) "${humanBytes(bps)}/s" else "-"
                _progressLabel.value = "다운로드 $percent%  ($leftPart, $speed)"
            }).onSuccess {
                _syncPhase.value = "이미지 저장 중…"
                _progressLabel.value = null
                loadPortraitsFromDb()
                routeAfterSync(isLoggedIn)
            }.onFailure { e ->
                Log.e(tag, "initialSync() 실패", e)
                _errorEvents.tryEmit("다운로드 중 오류가 발생하여 앱이 종료됩니다.")
                _exitApp.tryEmit(Unit)
            }
            _syncPhase.value = null
            _syncRunning.value = false
        }
    }

    /** 델타 동기화 */
    private fun startDeltaSyncThenRoute(isLoggedIn: Boolean) {
        if (_syncRunning.value) return
        _syncRunning.value = true
        _progressPercent.value = 0
        _syncPhase.value = "버전 확인 중…"

        viewModelScope.launch {
            repository.deltaSync(onProgressBytes = { done, total, read, totalBytes, bps ->
                val percent = if (totalBytes > 0) {
                    ((read.toDouble() / totalBytes.toDouble()) * 100).roundToInt()
                } else {
                    ((done.toDouble() / total.toDouble()) * 100).roundToInt()
                }.coerceIn(0, 100)
                _progressPercent.value = percent

                val leftPart = if (totalBytes > 0) {
                    "${humanBytes(read)} / ${humanBytes(totalBytes)}"
                } else {
                    "$done / $total 항목"
                }
                val speed = if (bps > 0) "${humanBytes(bps)}/s" else "-"
                _progressLabel.value = "다운로드 $percent%  ($leftPart, $speed)"
            }).onSuccess {
                _syncPhase.value = "이미지 저장 중..."
                _progressLabel.value = null
                loadPortraitsFromDb()
                routeAfterSync(isLoggedIn)
            }.onFailure { e ->
                Log.e(tag, "deltaSync() 실패", e)
                _errorEvents.tryEmit("업데이트 중 오류가 발생하여 앱이 종료됩니다.")
                _exitApp.tryEmit(Unit)
            }
            _syncPhase.value = null
            _syncRunning.value = false
        }
    }

    /** 동기화 후 네비 지정 */
    private suspend fun routeAfterSync(isLoggedIn: Boolean) {
        val onboardingDone: Boolean = prefsManager.onboardingCompletedFlow.firstOrNull() ?: false
        val dest = when {
            !onboardingDone -> Destination.ONBOARDING
            onboardingDone && isLoggedIn -> Destination.HOME
            else -> Destination.LOGIN
        }
        _destination.emit(dest)
    }

    /** 요원 이미지 불러오기 */
    private fun loadPortraitsFromDb() {
        viewModelScope.launch {
            runCatching { agentDao.getAllPortrait() }
                .onSuccess { agents ->
                    val paths = agents.mapNotNull { agent ->
                        agent.fullPortraitLocal
                            ?: agent.fullPortraitUrl
                    }
                    _portraitData.value = paths
                }
                .onFailure { e ->
                    Log.e(tag, "loadPortraitsFromDb() 실패", e)
                    _errorEvents.tryEmit("요원 이미지를 불러오지 못했습니다.")
                }
        }
    }

    /** 네트워크 연결 상태 출력 */
    private fun resolveNetworkType(): String {
        val connectivityManager =
            context.getSystemService(ConnectivityManager::class.java)
        val network = connectivityManager.activeNetwork ?: return "오프라인"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "오프라인"
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "모바일 데이터"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "유선(Ethernet)"
            else -> "기타 네트워크"
        }
    }

    /** 동의 완료 시 버전 저장 */
    fun acceptLatestPolicies() {
        viewModelScope.launch {
            prefsManager.setAcceptedPolicyVersions(
                termsVersion = TermsPolicy.REQUIRED_TERMS_VERSION,
                privacyVersion = TermsPolicy.REQUIRED_PRIVACY_VERSION
            )
        }
    }
}
