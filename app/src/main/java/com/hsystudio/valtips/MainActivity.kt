package com.hsystudio.valtips

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ValTips)
        super.onCreate(savedInstanceState)

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
}
