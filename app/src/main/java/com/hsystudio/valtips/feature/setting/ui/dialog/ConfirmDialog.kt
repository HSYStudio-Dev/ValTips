package com.hsystudio.valtips.feature.setting.ui.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.ui.component.DefaultButton
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.GradientMint
import com.hsystudio.valtips.ui.theme.GradientRed
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = TextWhite,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 취소 버튼
                DefaultButton(
                    text = dismissText,
                    startColor = ColorRed,
                    endColor = GradientRed,
                    borderColor = TextGray,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )

                // 확인 버튼
                DefaultButton(
                    text = confirmText,
                    startColor = GradientMint,
                    endColor = ColorMint,
                    borderColor = TextGray,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        containerColor = ColorBlack,
        modifier = Modifier.border(1.dp, TextGray, RoundedCornerShape(24.dp))
    )
}
