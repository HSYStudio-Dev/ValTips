package com.hsystudio.valtips.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.util.toCoilModel

@Composable
fun AgentCard(
    agent: AgentCardItem,
    onClick: (String) -> Unit,
    enabled: Boolean = true
) {
    // 비활성화 흑백 처리
    val colorMatrix = remember(enabled) {
        ColorMatrix().apply {
            setToSaturation(if (enabled) 1f else 0f)
        }
    }
    val model = remember(agent.agentIconLocal) { toCoilModel(agent.agentIconLocal) }

    Surface(
        onClick = { onClick(agent.uuid) },
        enabled = enabled,
        color = Color.Transparent,
        modifier = Modifier
            .aspectRatio(1f)
    ) {
        // 카드 테두리
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    BorderStroke(
                        width = 2.dp,
                        color = TextGray.copy(alpha = 0.60f),
                    )
                )
        ) {
            // 카드 배경
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6B7C84),
                                Color(0xFF181C1E)
                            )
                        )
                    )
                    .graphicsLayer(alpha = 0.70f)
            )
            // 요원 이미지
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.colorMatrix(colorMatrix)
            )
        }
    }
}
