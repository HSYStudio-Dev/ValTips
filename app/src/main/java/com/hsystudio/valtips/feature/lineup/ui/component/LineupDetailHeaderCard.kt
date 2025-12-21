package com.hsystudio.valtips.feature.lineup.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.feature.lineup.model.LineupDetailItem
import com.hsystudio.valtips.feature.lineup.model.LineupSideFilter
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.ColorStroke
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun LineupDetailHeaderCard(
    detail: LineupDetailItem
) {
    val sideLabel = when (detail.side) {
        LineupSideFilter.ATTACK -> "공격"
        LineupSideFilter.DEFENSE -> "수비"
        else -> "전체"
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ColorStroke),
        colors = CardDefaults.cardColors(containerColor = ColorBlack),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // 상단 칩들
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 공격/수비 칩
                LabelChip(
                    text = sideLabel,
                    backgroundColor = ColorRed
                )

                // 사이트 칩
                LabelChip(
                    text = detail.site,
                    backgroundColor = ColorMint
                )
            }

            Spacer(Modifier.height(12.dp))

            // 제목
            Text(
                text = detail.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            // 작성자 + 날짜
            Text(
                text = "작성자: ${detail.writer} | ${detail.updatedAt}",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )

            Spacer(Modifier.height(8.dp))

            // 설명
            Text(
                text = detail.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite
            )
        }
    }
}
