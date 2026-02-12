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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.ui.component.DefaultButton
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.GradientRed
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun MaintenanceDialog(
    message: String,
    onExit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {  },

        title = {
            Text(
                text = "점검 안내",
                color = TextWhite,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
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
                    text = message,
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "점검이 완료된 후 다시 이용해 주세요.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        },

        confirmButton = {
            DefaultButton(
                text = "종료",
                startColor = ColorRed,
                endColor = GradientRed,
                borderColor = ColorRed,
                onClick = onExit
            )
        },

        modifier = Modifier
            .border(1.dp, TextGray, RoundedCornerShape(24.dp))
    )
}
