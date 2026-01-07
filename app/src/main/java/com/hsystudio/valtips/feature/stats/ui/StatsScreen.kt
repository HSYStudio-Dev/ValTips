package com.hsystudio.valtips.feature.stats.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hsystudio.valtips.R
import com.hsystudio.valtips.feature.stats.viewmodel.StatsViewModel
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.TextGray

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Stats")
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_comingsoon),
                        contentDescription = "전적 준비중",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(512f / 325f)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "아직 전적 기능은 준비 중입니다!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextGray
                    )
                }
            }
        }
    }
}
