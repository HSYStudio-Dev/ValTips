package com.hsystudio.valtips.feature.login.ui.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "• 로그인 시 닉네임, 티어 등 게임 정보가 앱 내에 표시됩니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• 계정 비밀번호는 절대 저장하거나 수집하지 않으며, 모든 인증은 라이엇 공식 시스템에서 수행됩니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• 개인정보 보호를 위해 Riot 로그인 상태는 주기적으로 갱신이 필요할 수 있습니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• 본 앱은 Riot Games 또는 VALORANT와 공식적으로 제휴되지 않은 팬 메이드 앱입니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            DefaultButton(
                text = "확인",
                startColor = ColorMint,
                endColor = GradientMint,
                borderColor = ColorMint,
                onClick = onDismiss
            )
        },
        modifier = Modifier.border(1.dp, TextGray, RoundedCornerShape(24.dp))
    )
}
