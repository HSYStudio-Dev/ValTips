package com.hsystudio.valtips.feature.map.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsystudio.valtips.feature.map.viewmodel.MapsViewModel
import com.hsystudio.valtips.ui.component.MapCard
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.TextGray

@Composable
fun MapsScreen(
    onMapClick: (String) -> Unit,
    viewModel: MapsViewModel = hiltViewModel()
) {
    val mapsUi by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(title = "Maps")
        }
    ) { values ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = values.calculateTopPadding())
        ) {
            val horizontalPadding = when {
                maxWidth < 400.dp -> 24.dp
                maxWidth < 600.dp -> 32.dp
                else -> 40.dp
            }
            val verticalPadding = when {
                maxHeight < 600.dp -> 24.dp
                maxHeight < 800.dp -> 32.dp
                else -> 40.dp
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 섹션: 현재 액트
                item {
                    mapsUi.actTitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                textDecoration = TextDecoration.Underline
                            ),
                            color = TextGray,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // 시즌 활성화 맵
                items(mapsUi.activeMaps, key = { it.uuid }) { map ->
                    MapCard(
                        item = map,
                        active = true,
                        onClick = onMapClick
                    )
                }

                // 섹션: 제외된 맵
                if (mapsUi.retiredMaps.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "제외된 맵",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                textDecoration = TextDecoration.Underline
                            ),
                            color = TextGray,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // 시즌 비활성화 맵
                    items(mapsUi.retiredMaps, key = { it.uuid }) { map ->
                        MapCard(
                            item = map,
                            active = false,
                            onClick = onMapClick
                        )
                    }
                }
            }
        }
    }
}
