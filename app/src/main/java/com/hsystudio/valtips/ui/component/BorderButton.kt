package com.hsystudio.valtips.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.ui.theme.ColorRed

@Composable
fun BorderButton(
    text: String,
    btnColor: Color = ColorRed,
    onClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val borderColor = Color.White
    val framePadding = 6.dp
    val buttonHeight = 48.dp

    Box(
        modifier = modifier
            .height(buttonHeight + framePadding * 2)
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val gapHeight = 12.dp.toPx().coerceAtMost(size.height)
                val topY = (size.height - gapHeight) / 2f
                val bottomY = topY + gapHeight
                val width = size.width
                val height = size.height

                // 위쪽 라인
                drawLine(
                    color = borderColor,
                    start = Offset(0f, strokeWidth / 2),
                    end = Offset(width, strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
                // 아래쪽 라인
                drawLine(
                    color = borderColor,
                    start = Offset(0f, height - strokeWidth / 2),
                    end = Offset(width, height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
                // 왼쪽 라인 (위 절반)
                drawLine(
                    color = borderColor,
                    start = Offset(strokeWidth / 2, 0f),
                    end = Offset(strokeWidth / 2, topY),
                    strokeWidth = strokeWidth
                )
                // 왼쪽 라인 (아래 절반)
                drawLine(
                    color = borderColor,
                    start = Offset(strokeWidth / 2, bottomY),
                    end = Offset(strokeWidth / 2, height),
                    strokeWidth = strokeWidth
                )
                // 오른쪽 라인 (위 절반)
                drawLine(
                    color = borderColor,
                    start = Offset(width - strokeWidth / 2, 0f),
                    end = Offset(width - strokeWidth / 2, topY),
                    strokeWidth = strokeWidth
                )
                // 오른쪽 라인 (아래 절반)
                drawLine(
                    color = borderColor,
                    start = Offset(width - strokeWidth / 2, bottomY),
                    end = Offset(width - strokeWidth / 2, height),
                    strokeWidth = strokeWidth
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
            shape = RectangleShape,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(framePadding)
                .height(buttonHeight)
        ) {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun BorderButtonPreView() {
    BorderButton(
        text = "Riot ID로 로그인",
        onClick = {}
    )
}
