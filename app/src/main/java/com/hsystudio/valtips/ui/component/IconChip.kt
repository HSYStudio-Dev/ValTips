package com.hsystudio.valtips.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hsystudio.valtips.R
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.util.toCoilModel

@Composable
fun IconChip(
    isSelected: Boolean,
    isAll: Boolean,
    iconLocal: String?,
    labelWhenNoIcon: String? = null,
    btnSize: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    onClick: () -> Unit
) {
    val tealAlpha = 0.30f
    val whiteAlpha by animateFloatAsState(if (isSelected) 0.70f else 0.50f, label = "whiteAlpha")
    val strokeAlpha by animateFloatAsState(if (isSelected) 0.80f else 0.30f, label = "strokeAlpha")
    val strokeWidth = if (isSelected) 3.dp else 2.dp

    val model = remember(iconLocal) { toCoilModel(iconLocal) }

    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(0),
        tonalElevation = 0.dp,
        modifier = Modifier.size(btnSize)
    ) {
        // 버튼 배경
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokePx = strokeWidth.toPx()

            // 흰색 배경
            drawRoundRect(
                color = Color.White.copy(alpha = whiteAlpha)
            )
            // 메인 색상 배경
            drawRoundRect(
                color = ColorMint.copy(alpha = tealAlpha),
            )
            // 테두리
            drawRoundRect(
                color = Color.White.copy(alpha = strokeAlpha),
                style = Stroke(width = strokePx),
                topLeft = Offset(strokePx / 2f, strokePx / 2f),
                size = Size(size.width - strokePx, size.height - strokePx)
            )
        }

        // 아이콘
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                // 역할 전체
                isAll -> {
                    Icon(
                        painter = painterResource(id = R.drawable.role_all),
                        contentDescription = "전체",
                        tint = Color.Unspecified
                    )
                }

                // 아이콘 이미지 있을 경우
                model != null -> {
                    AsyncImage(
                        model = model,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        contentScale = ContentScale.Fit
                    )
                }

                // 아이콘 이미지 없을 경우
                !labelWhenNoIcon.isNullOrBlank() -> {
                    Icon(
                        painter = painterResource(id = R.drawable.passive),
                        contentDescription = "패시브",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(iconSize)
                    )
                }

                else -> Unit
            }
        }
    }
}
