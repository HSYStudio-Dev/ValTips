package com.hsystudio.valtips.feature.map.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.feature.map.model.MapRecommendedAgentItem
import com.hsystudio.valtips.ui.component.AgentCard
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorStroke
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun RecommendedAgentsSection(
    agents: List<MapRecommendedAgentItem>,
    size: Dp
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(1.dp, ColorStroke),
        colors = CardDefaults.cardColors(containerColor = ColorBlack),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "추천 조합",
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            if (agents.isEmpty()) {
                Text(
                    text = "등록된 추천 조합이 아직 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    agents.forEach { agent ->
                        Box(
                            modifier = Modifier.size(size)
                        ) {
                            AgentCard(
                                agent = AgentCardItem(
                                    uuid = agent.uuid,
                                    roleUuid = null,
                                    agentIconLocal = agent.iconLocal,
                                ),
                                onClick = {},
                                enabled = true
                            )
                        }
                    }
                }
            }
        }
    }
}
