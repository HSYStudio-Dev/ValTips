package com.hsystudio.valtips.ui.component.ad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AdBackgroundColor = Color(0xFF2C3136)
private val AdPrimaryTextColor = Color(0xFFFFFFFF)
private val AdSecondaryTextColor = Color(0xFFB0B0B0)
private val AD_HEIGHT = 64.dp

@Composable
fun AdLoadingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AD_HEIGHT)
            .background(AdBackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = AdSecondaryTextColor
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "광고 로딩 중...",
                style = MaterialTheme.typography.bodySmall,
                color = AdSecondaryTextColor,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun AdErrorPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AD_HEIGHT)
            .background(AdBackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AdSecondaryTextColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "광고를 불러오지 못했습니다",
                    style = MaterialTheme.typography.labelMedium,
                    color = AdPrimaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "네트워크 상태를 확인해 주세요",
                    style = MaterialTheme.typography.labelSmall,
                    color = AdSecondaryTextColor,
                    fontSize = 10.sp
                )
            }
        }
    }
}
