package com.hsystudio.valtips.feature.lineup.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.ui.component.AgentCard
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.util.toCoilModel

@Composable
fun LineupHeaderCard(
    agentIcon: String?,
    mapSplash: String?,
    placeholderIcon: String?,
    boxHeight: Int
) {
    val cardHeight = boxHeight + (16 * 2)
    val mapModel = remember(mapSplash, placeholderIcon) {
        toCoilModel(mapSplash ?: placeholderIcon)
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ColorBlack),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 요원 아이콘
            Box(
                modifier = Modifier
                    .size(boxHeight.dp)
                    .background(ColorBlack)
            ) {
                AgentCard(
                    agent = AgentCardItem(
                        uuid = "",
                        roleUuid = null,
                        agentIconLocal = agentIcon ?: placeholderIcon
                    ),
                    onClick = {}
                )
            }

            // 맵 스플래시
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(boxHeight.dp)
                    .background(Color(0xFF77858F).copy(0.5f))
            ) {
                AsyncImage(
                    model = mapModel,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillHeight
                )
            }
        }
    }
}
