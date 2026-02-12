package com.hsystudio.valtips.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.GradientBlack
import com.hsystudio.valtips.ui.theme.GradientMint

@Composable
fun DefaultButton(
    text: String,
    startColor: Color,
    endColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(startColor, endColor)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(brush = gradientBrush)
            .border(
                BorderStroke(0.5.dp, borderColor),
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun DefaultButtonMintPreView() {
    DefaultButton(
        text = "프로필 이미지 변경",
        startColor = ColorMint,
        endColor = GradientMint,
        borderColor = ColorMint,
        onClick = {}
    )
}

@Preview
@Composable
fun DefaultButtonBlackPreView() {
    DefaultButton(
        text = "프로필 이미지 변경",
        endColor = ColorBlack,
        startColor = GradientBlack,
        borderColor = ColorBlack,
        onClick = {}
    )
}
