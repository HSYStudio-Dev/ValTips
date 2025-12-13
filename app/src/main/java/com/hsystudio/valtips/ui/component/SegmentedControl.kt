package com.hsystudio.valtips.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun <T> SegmentedControl(
    items: List<SegmentItem<T>>,
    selected: T,
    onSelected: (T) -> Unit,
    height: Dp = 52.dp,
    outerRadius: Dp = 16.dp,
    innerRadius: Dp = 12.dp
) {
    val dark = Color(0xFF204341)
    val mint = ColorMint
    val base = Color.Transparent

    // 외부 버튼 테두리
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(outerRadius))
            .border(1.dp, TextGray, RoundedCornerShape(outerRadius))
            .background(ColorBlack)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                val isSelected = selected == item.value

                // 선택 여부에 따른 애니메이션
                val progress by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    label = "segProgress_${item.label}"
                )

                // 버튼별 배경 스펙
                val (targetStart, targetMiddle, targetEnd) =
                    item.gradientSpec(dark, mint)

                val startColor = lerp(base, targetStart, progress)
                val endColor = lerp(base, targetEnd, progress)
                val middleColor = targetMiddle?.let { mid ->
                    lerp(base, mid, progress)
                }

                val brush = if (middleColor != null) {
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.0f to startColor,
                            0.5f to middleColor,
                            1.0f to endColor
                        )
                    )
                } else {
                    Brush.horizontalGradient(colors = listOf(startColor, endColor))
                }

                // 내부 버튼
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(innerRadius))
                        .background(brush)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) ColorMint else Color.Transparent,
                            shape = RoundedCornerShape(innerRadius)
                        )
                        .clickable { onSelected(item.value) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) TextWhite else TextGray
                    )
                }
            }
        }
    }
}

data class SegmentItem<T>(
    val value: T,
    val label: String,
    val gradientSpec: (dark: Color, mint: Color) -> Triple<Color, Color?, Color>
)
