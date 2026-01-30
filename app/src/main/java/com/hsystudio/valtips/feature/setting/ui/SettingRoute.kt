package com.hsystudio.valtips.feature.setting.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsystudio.valtips.feature.setting.SettingUiEffect
import com.hsystudio.valtips.feature.setting.viewmodel.SettingViewModel
import com.hsystudio.valtips.util.openCustomTab

@Composable
fun SettingRoute(
    onMembershipClick: () -> Unit,
    onNavigateToSplash: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val adState by viewModel.nativeAdState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingUiEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is SettingUiEffect.OpenCustomTab -> {
                    openCustomTab(context, effect.url)
                }
                is SettingUiEffect.NavigateToMembership -> {
                    onMembershipClick()
                }
                is SettingUiEffect.NavigateToSplash -> {
                    onNavigateToSplash()
                }
            }
        }
    }

    SettingScreen(
        uiState = uiState,
        adState = adState,
        onEvent = viewModel::onEvent
    )
}
