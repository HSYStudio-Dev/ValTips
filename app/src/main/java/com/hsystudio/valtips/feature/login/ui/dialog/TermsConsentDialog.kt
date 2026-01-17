package com.hsystudio.valtips.feature.login.ui.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.ui.component.DefaultButton
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.GradientMint
import com.hsystudio.valtips.ui.theme.GradientRed
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun TermsConsentDialog(
    isReConsent: Boolean,
    onOpenTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    var agreedTerms by remember { mutableStateOf(false) }
    var agreedPrivacy by remember { mutableStateOf(false) }

    val canConfirm = agreedTerms && agreedPrivacy

    val title = if (isReConsent) "약관이 변경되었습니다" else "약관 동의"
    val desc = if (isReConsent) {
        "서비스 이용을 위해 변경된 약관을 확인하고\n다시 동의해 주세요."
    } else {
        "서비스 이용을 위해 아래 약관을 확인하고\n동의해 주세요."
    }

    AlertDialog(
        onDismissRequest = {  },
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
                    text = desc,
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                // 이용약관
                ConsentRow(
                    checked = agreedTerms,
                    label = "(필수) 이용약관 동의",
                    onToggle = { agreedTerms = !agreedTerms },
                    onOpen = onOpenTerms
                )

                // 개인정보처리방침
                ConsentRow(
                    checked = agreedPrivacy,
                    label = "(필수) 개인정보처리방침 동의",
                    onToggle = { agreedPrivacy = !agreedPrivacy },
                    onOpen = onOpenPrivacy
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DefaultButton(
                    text = "취소",
                    startColor = ColorRed,
                    endColor = GradientRed,
                    borderColor = TextGray,
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )

                DefaultButton(
                    text = "확인",
                    startColor = GradientMint,
                    endColor = ColorMint,
                    borderColor = TextGray,
                    onClick = { if (canConfirm) onConfirm() },
                    modifier = Modifier
                        .weight(1f)
                        .alpha(if (canConfirm) 1f else 0.4f)
                )
            }
        },
        modifier = Modifier.border(1.dp, TextGray, RoundedCornerShape(24.dp))
    )
}

@Composable
private fun ConsentRow(
    checked: Boolean,
    label: String,
    onToggle: () -> Unit,
    onOpen: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() }
        )

        Text(
            text = label,
            color = TextWhite,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.weight(1f))

        // 내용 보기 버튼
        Box(
            modifier = Modifier
                .size(36.dp)
                .clickable { onOpen() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "열기",
                tint = TextWhite
            )
        }
    }
}
