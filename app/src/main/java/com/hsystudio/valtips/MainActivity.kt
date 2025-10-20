package com.hsystudio.valtips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.hsystudio.valtips.navigation.AppNavGraph
import com.hsystudio.valtips.ui.theme.ValTipsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ValTips)
        super.onCreate(savedInstanceState)
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
}
