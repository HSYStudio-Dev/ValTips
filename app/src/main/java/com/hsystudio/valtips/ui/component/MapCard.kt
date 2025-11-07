package com.hsystudio.valtips.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hsystudio.valtips.domain.model.MapListItem
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.ColorStroke
import com.hsystudio.valtips.ui.theme.Spoqa
import com.hsystudio.valtips.ui.theme.TextWhite
import com.hsystudio.valtips.ui.theme.Valorant
import com.hsystudio.valtips.util.toCoilModel

@Composable
fun MapCard(
    item: MapListItem,
    active: Boolean,
    onClick: (String) -> Unit,
    enabled: Boolean = true
) {
    // 비활성화 흑백 처리
    val colorMatrix = remember(enabled) {
        ColorMatrix().apply {
            setToSaturation(if (enabled) 1f else 0f)
        }
    }
    val model = remember(item.listImageLocal) { toCoilModel(item.listImageLocal) }

    Card(
        onClick = { onClick(item.uuid) },
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ColorStroke),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            // 배경 이미지
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(colorMatrix)
            )
            // 그라데이션 배경
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, ColorBlack),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            // 맵명 + 활성화 상태
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val eng = item.englishName.orEmpty()
                val kor = item.displayName
                Text(
                    text = buildAnnotatedString {
                        if (eng.isNotBlank()) {
                            // 영문
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = Valorant,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 20.sp,
                                    color = TextWhite
                                )
                            ) { append(eng) }
                            // 경계선
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = Spoqa,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 20.sp,
                                    color = TextWhite
                                )
                            ) { append(" | ") }
                        }
                        // 한글
                        withStyle(
                            style = SpanStyle(
                                fontFamily = Spoqa,
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                color = TextWhite
                            )
                        ) { append(kor) }
                    },
                    modifier = Modifier.weight(1f)
                )

                // 활성화 상태
                StatusDot(
                    active = if (enabled) {
                        active
                    } else {
                        false
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusDot(active: Boolean) {
    val color = if (active) ColorMint else ColorRed
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(RoundedCornerShape(100))
            .background(color)
    )
}
