package com.hsystudio.valtips.feature.login.ui.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.ui.component.DefaultButton
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.GradientMint
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

// Todo : RSO 연동 후 삭제
@Composable
fun RiotLoginPreparingDialog(
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onConfirm,
        title = {
            Text(
                text = "Riot ID 로그인 준비 중",
                color = TextWhite,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "Riot ID 로그인 기능은 현재 준비 중입니다.\n로그인 없이 시작으로 서비스를 이용해 주세요.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            DefaultButton(
                text = "확인",
                startColor = ColorMint,
                endColor = GradientMint,
                borderColor = ColorMint,
                onClick = onConfirm
            )
        },
        modifier = Modifier.border(1.dp, TextGray, RoundedCornerShape(24.dp))
    )
}
