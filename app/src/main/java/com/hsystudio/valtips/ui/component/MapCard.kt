package com.hsystudio.valtips.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import coil3.size.Size
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
    val context = LocalContext.current

    // 색상 매트릭스
    val colorMatrix = remember(enabled) {
        ColorMatrix().apply {
            setToSaturation(if (enabled) 1f else 0f)
        }
    }

    // Coil 요청 객체 생성
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(toCoilModel(item.listImageLocal))
            .scale(Scale.FILL)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build()
    )

    // 그라데이션 브러쉬 캐싱
    val gradientBrush = remember {
        Brush.verticalGradient(
            colors = listOf(Color.Transparent, ColorBlack),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    }

    // 텍스트 캐싱
    val annotatedName = remember(item.uuid, item.displayName, item.englishName) {
        val eng = item.englishName.orEmpty()
        val kor = item.displayName

        buildAnnotatedString {
            if (eng.isNotBlank()) {
                withStyle(
                    style = SpanStyle(
                        fontFamily = Valorant,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        color = TextWhite
                    )
                ) { append(eng) }
                withStyle(
                    style = SpanStyle(
                        fontFamily = Spoqa,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = TextWhite
                    )
                ) { append(" | ") }
            }
            withStyle(
                style = SpanStyle(
                    fontFamily = Spoqa,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    color = TextWhite
                )
            ) { append(kor) }
        }
    }

    Card(
        onClick = { onClick(item.uuid) },
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ColorStroke),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            // 배경 이미지
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(colorMatrix)
            )

            // 그라데이션 배경
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = gradientBrush)
            )

            // 맵명 + 활성화 상태
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 맵명
                Text(
                    text = annotatedName,
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
    Spacer(
        modifier = Modifier
            .size(16.dp)
            .drawBehind {
                drawCircle(color = color)
            }
    )
}
