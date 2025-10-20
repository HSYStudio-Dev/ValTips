package com.hsystudio.valtips.feature.login.ui.dialog

import android.annotation.SuppressLint
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
import androidx.compose.ui.unit.sp
import com.hsystudio.valtips.ui.component.DefaultButton
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@SuppressLint("DefaultLocale")
@Composable
fun DownloadConfirmDialog(
    sizeMb: Double?,
    networkType: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {  },
        title = {
            Text(
                text = "리소스 다운로드",
                color = TextWhite,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${sizeMb?.let { String.format("%.2f", it) } ?: "--"}MB",
                    color = ColorRed,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    "필수 리소스 다운로드가 필요합니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    "확인을 누르면 다운로드를 시작합니다.\n취소를 누르면 앱이 종료됩니다.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    "현재 네트워크: $networkType",
                    color = ColorMint,
                    style = MaterialTheme.typography.bodyLarge,
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
                DefaultButton(
                    text = "취소",
                    buttonColor = ColorRed,
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
                DefaultButton(
                    text = "확인",
                    buttonColor = ColorMint,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        modifier = Modifier.border(1.dp, TextGray, RoundedCornerShape(24.dp))
    )
}
