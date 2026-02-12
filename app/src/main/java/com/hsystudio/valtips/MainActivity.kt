package com.hsystudio.valtips

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.hsystudio.valtips.data.auth.AuthTokenBus
import com.hsystudio.valtips.data.auth.LoginDeepLinkParser
import com.hsystudio.valtips.navigation.AppNavGraph
import com.hsystudio.valtips.ui.theme.ValTipsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authTokenBus: AuthTokenBus

    // 업데이트 매니저 및 요청 코드 선언
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ValTips)
        super.onCreate(savedInstanceState)

        // 업데이트 매니저 초기화
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // 업데이트 체크 실행
        checkForAppUpdate()

        handleDeepLink(intent)

        setContent {
            ValTipsTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    onExitApp = { finishAffinity() },
                    navController = navController
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val token = LoginDeepLinkParser.parseToken(intent?.data) ?: return
        authTokenBus.emit(token)
    }

    // 업데이트 확인 및 실행 함수
    private fun checkForAppUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // 업데이트가 있고 + 즉시 업데이트가 허용된 경우 -> 업데이트 화면 띄우기
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    updateRequestCode
                )
            }
        }
    }

    // 앱이 다시 활성화 될 때(onResume) 업데이트가 진행 중인지 확인
    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            // 즉시 업데이트가 진행 중인데 도중에 앱이 내려갔다가 다시 올라온 경우
            if (appUpdateInfo.updateAvailability()
                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                // 다시 업데이트 전체 화면을 띄워서 강제로 진행
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    updateRequestCode
                )
            }
        }
    }

    // 업데이트 화면에서 돌아왔을 때 결과 처리
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == updateRequestCode) {
            if (resultCode != RESULT_OK) {
                // 사용자가 업데이트를 거부했거나 취소한 경우
                Toast.makeText(this, "원활한 사용을 위해 업데이트가 필요합니다.", Toast.LENGTH_LONG).show()

                finish()
            }
        }
    }
}
