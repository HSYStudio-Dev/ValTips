package com.hsystudio.valtips.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint

@Composable
fun DefaultButton(
    text: String,
    buttonColor: Color,
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, Color.White),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun DefaultButtonMintPreView() {
    DefaultButton(
        text = "프로필 이미지 변경",
        buttonColor = ColorMint,
        onClick = {}
    )
}

@Preview
@Composable
fun DefaultButtonBlackPreView() {
    DefaultButton(
        text = "프로필 이미지 변경",
        buttonColor = ColorBlack,
        onClick = {}
    )
}
