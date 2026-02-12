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
import com.hsystudio.valtips.domain.repository.SystemRepository
import com.hsystudio.valtips.feature.login.model.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
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
    private val systemRepository: SystemRepository,
    private val resourceRepository: ResourceRepository,
    private val agentDao: AgentDao
) : ViewModel() {
    private val tag = "LoginViewModel"

    // --- 점검 다이얼로그 --- //
    private val _showMaintenanceDialog = MutableStateFlow(false)
    val showMaintenanceDialog: StateFlow<Boolean> = _showMaintenanceDialog.asStateFlow()

    private val _maintenanceMessage = MutableStateFlow<String?>(null)
    val maintenanceMessage: StateFlow<String?> = _maintenanceMessage.asStateFlow()

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

    // 약관 재동의 다이얼로그 출력 여부
    private val _showPolicyReConsentDialog = MutableStateFlow(false)
    val showPolicyReConsentDialog: StateFlow<Boolean> =
        _showPolicyReConsentDialog.asStateFlow()

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
    // 함수 정의
    // ─────────────────────────────
    // 온보딩 완료 저장
    fun setOnboardingCompleted() {
        viewModelScope.launch { prefsManager.setOnboardingCompleted(true) }
    }

    /**
     * 앱 시작 로직
     * 1. 서버 상태 점검 (API 호출)
     * 2. 점검중(true) -> 다이얼로그 출력 후 정지
     * 3. 정상(false) -> 기존 동기화 로직(checkAndRunSync) 실행
     */
    fun runStartupFlow() {
        if (_syncRunning.value) return

        viewModelScope.launch {
            _syncRunning.value = true
            _syncPhase.value = "서버 상태 확인 중…"

            // 1. 서버 점검 상태 확인
            val statusResult = systemRepository.getSystemStatus()

            val status = statusResult.getOrNull()
            // 통신 실패
            if (status == null) {
                Log.e(tag, "runStartupFlow() 실패", statusResult.exceptionOrNull())
                _syncRunning.value = false
                _syncPhase.value = null
                _errorEvents.tryEmit("네트워크 연결이 불안정하여 앱이 종료됩니다.")
                _exitApp.tryEmit(Unit)
                return@launch
            }

            if (status.isMaintenance) {
                // 2. [점검중] 다이얼로그 출력
                _maintenanceMessage.value = status.maintenanceMessage
                _showMaintenanceDialog.value = true
                _syncRunning.value = false
                _syncPhase.value = null
                return@launch
            }

            // 3. [정상] 기존 동기화 로직 실행
            _syncRunning.value = false
            checkAndRunSync()
        }
    }

    // 점검 다이얼로그 확인 버튼 클릭 시 앱 종료
    fun onConfirmMaintenance() {
        _showMaintenanceDialog.value = false
        _maintenanceMessage.value = null
        _exitApp.tryEmit(Unit)
    }

    /**
     * 버전 체크 & 동기화 분기
     * - lastSync가 없으면: 용량 조회 → 동의 다이얼로그 표시
     * - lastSync가 있으면: 델타 동기화 바로 시작
     */
    private suspend fun checkAndRunSync() {
        if (_syncRunning.value) return

        try {
            val last = prefsManager.lastSyncFlow.firstOrNull()
            if (last.isNullOrBlank()) {
                // 초기 실행: 용량 조회 + 동의 다이얼로그
                _syncPhase.value = "리소스 정보 확인 중…"
                resourceRepository.getResourceSize()
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
                startDeltaSyncThenRoute()
            }
        } catch (e: Exception) {
            Log.e(tag, "checkAndRunSync() 실패", e)
            _errorEvents.tryEmit("네트워크 오류로 인해 앱이 종료됩니다.")
            _exitApp.tryEmit(Unit)
        }
    }

    /** 다이얼로그 취소 → 앱 종료 */
    fun cancelInitialDownload() {
        _downloadDialogVisible.value = false
        _errorEvents.tryEmit("필수 리소스를 다운받지 않아 앱이 종료됩니다.")
        _exitApp.tryEmit(Unit)
    }

    /** 다이얼로그 확인 → 초기 동기화 시작 */
    fun confirmInitialDownload() {
        _downloadDialogVisible.value = false
        startInitialSyncThenRoute()
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
    private fun startInitialSyncThenRoute() {
        if (_syncRunning.value) return
        _syncRunning.value = true
        _progressPercent.value = 0
        _syncPhase.value = "필수 리소스 다운로드 중…"

        viewModelScope.launch {
            resourceRepository.initialSync(onProgressBytes = { done, total, read, totalBytes, bps ->
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
                routeAfterSync()
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
    private fun startDeltaSyncThenRoute() {
        if (_syncRunning.value) return
        _syncRunning.value = true
        _progressPercent.value = 0
        _syncPhase.value = "버전 확인 중…"

        viewModelScope.launch {
            resourceRepository.deltaSync(onProgressBytes = { done, total, read, totalBytes, bps ->
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
                routeAfterSync()
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
    private suspend fun routeAfterSync() {
        // 온보딩 완료 여부
        val onboardingDone: Boolean = prefsManager.onboardingCompletedFlow.firstOrNull() ?: false
        // 현재 약관 동의 상태 값
        val termsVersion = prefsManager.acceptedTermsVersionFlow.firstOrNull()
        val privacyVersion = prefsManager.acceptedPrivacyVersionFlow.firstOrNull()
        // 약관 동의 기록 유무 체크
        val hasAnyConsent = !termsVersion.isNullOrBlank() || !privacyVersion.isNullOrBlank()
        // 약관 버전 체크
        val isLatest =
            termsVersion == TermsPolicy.REQUIRED_TERMS_VERSION &&
                privacyVersion == TermsPolicy.REQUIRED_PRIVACY_VERSION

        // 온보딩 미완료 → 무조건 온보딩
        if (!onboardingDone) {
            _destination.emit(Destination.ONBOARDING)
            return
        }

        // 온보딩 완료 + 동의 기록 없음 → 로그인 화면
        if (!hasAnyConsent) {
            _destination.emit(Destination.LOGIN)
            return
        }

        // 동의 기록은 있으나 버전 불일치 → 재동의 다이얼로그 출력
        if (!isLatest) {
            _showPolicyReConsentDialog.value = true
            return
        }

        // 모든 조건 통과 → 홈
        _destination.emit(Destination.HOME)
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

    /** 약관 동의 완료 시 버전 저장(로그인) */
    fun acceptLatestPolicies() {
        viewModelScope.launch {
            prefsManager.setAcceptedPolicyVersions(
                termsVersion = TermsPolicy.REQUIRED_TERMS_VERSION,
                privacyVersion = TermsPolicy.REQUIRED_PRIVACY_VERSION
            )
        }
    }

    /** 약관 재동의 다이얼로그 확인 처리(스플래시) */
    fun confirmReConsent() {
        viewModelScope.launch {
            prefsManager.setAcceptedPolicyVersions(
                termsVersion = TermsPolicy.REQUIRED_TERMS_VERSION,
                privacyVersion = TermsPolicy.REQUIRED_PRIVACY_VERSION
            )
            _showPolicyReConsentDialog.value = false
            _destination.emit(Destination.HOME)
        }
    }

    /** 약관 재동의 다이얼로그 취소 처리(스플래시) */
    fun cancelReConsent() {
        _showPolicyReConsentDialog.value = false
        _errorEvents.tryEmit("약관에 동의하지 않아 앱이 종료됩니다.")
        _exitApp.tryEmit(Unit)
    }
}
