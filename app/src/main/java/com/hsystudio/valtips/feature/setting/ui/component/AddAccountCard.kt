package com.hsystudio.valtips.feature.setting.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.ui.component.DefaultButton
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorStroke
import com.hsystudio.valtips.ui.theme.GradientMint
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun AddAccountCard(
    enabled: Boolean,
    onAdd: () -> Unit
) {
    val borderColor = if (enabled) ColorStroke else ColorStroke.copy(0.5f)
    val lockText = if (enabled) "🔓" else "🔒"

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = ColorBlack),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 잠금 상태
            Text(
                text = lockText,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.width(8.dp))

            // 안내 문구
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "계정 추가 (최대 3개)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "프로 멤버십 전용",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
            }

            // 계정 추가 버튼
            Box(
                modifier = Modifier.alpha(if (enabled) 1f else 0.45f)
            ) {
                DefaultButton(
                    text = "계정 추가",
                    startColor = ColorMint,
                    endColor = GradientMint,
                    borderColor = ColorMint,
                    onClick = { onAdd() },
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}
