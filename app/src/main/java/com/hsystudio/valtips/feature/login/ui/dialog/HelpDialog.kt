package com.hsystudio.valtips.feature.login.ui.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun HelpDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "안내 사항",
                color = TextWhite,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "• 로그인 후 닉네임, 프로필 이미지 등 일부 정보가 앱 내에서 공개될 수 있습니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• 계정, 비밀번호 등 민감한 정보는 저장하거나 수집하지 않습니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• Riot ID 로그인의 인증 과정은 Riot Games의 공식 인증 시스템을 통해 처리됩니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• 안심하고 로그인해 주세요. 다만 Riot 계정 보안 정책을 항상 준수해 주세요.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• 본 앱은 Riot Games 또는 VALORANT와 공식적으로 연관이 없습니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            DefaultButton(
                text = "확인",
                buttonColor = ColorMint,
                onClick = onDismiss
            )
        },
        modifier = Modifier.border(1.dp, TextGray, RoundedCornerShape(24.dp))
    )
}
