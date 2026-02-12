package com.hsystudio.valtips.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
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
    val context = LocalContext.current

    // 애니메이션 값
    val whiteAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.70f else 0.50f,
        label = "whiteAlpha"
    )
    val strokeAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.80f else 0.30f,
        label = "strokeAlpha"
    )
    val strokeWidth = if (isSelected) 3.dp else 2.dp

    // Coil 요청 객체 생성
    val painter = if (!isAll && iconLocal != null) {
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(toCoilModel(iconLocal))
                .size(coil3.size.Size.ORIGINAL)
                .crossfade(true)
                .build()
        )
    } else {
        null
    }

    // 버튼 배경 + 테두리
    Box(
        modifier = Modifier
            .size(btnSize)
            .clickable(onClick = onClick)
            .drawBehind {
                val strokePx = strokeWidth.toPx()
                val width = size.width
                val height = size.height

                // 흰색 배경
                drawRect(
                    color = Color.White.copy(alpha = whiteAlpha)
                )
                // 메인 색상 배경
                drawRect(
                    color = ColorMint.copy(alpha = 0.30f)
                )
                // 테두리
                drawRect(
                    color = Color.White.copy(alpha = strokeAlpha),
                    style = Stroke(width = strokePx),
                    topLeft = Offset(strokePx / 2f, strokePx / 2f),
                    size = Size(width - strokePx, height - strokePx)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // 아이콘 표시 로직
        when {
            // 역할 전체 아이콘
            isAll -> {
                Icon(
                    painter = painterResource(id = R.drawable.role_all),
                    contentDescription = "전체",
                    tint = Color.Unspecified
                )
            }

            // 아이콘 이미지가 있는 경우
            painter != null -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    contentScale = ContentScale.Fit
                )
            }

            // 아이콘 이미지가 없는 경우 (패시브)
            !labelWhenNoIcon.isNullOrBlank() -> {
                Icon(
                    painter = painterResource(id = R.drawable.passive),
                    contentDescription = "패시브",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}
