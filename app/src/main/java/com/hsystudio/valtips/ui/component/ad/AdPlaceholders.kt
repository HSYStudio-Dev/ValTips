package com.hsystudio.valtips.ui.component.ad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsystudio.valtips.ui.theme.TextBlack
import com.hsystudio.valtips.ui.theme.TextGray

private val AD_HEIGHT = 80.dp

@Composable
fun AdLoadingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AD_HEIGHT)
            .background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = TextGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "광고 로딩 중...",
                style = MaterialTheme.typography.bodySmall,
                color = TextBlack
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
            .background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "광고를 불러오지 못했습니다",
                style = MaterialTheme.typography.labelMedium,
                color = TextBlack
            )
            Text(
                text = "네트워크 상태를 확인해 주세요",
                style = MaterialTheme.typography.labelSmall,
                color = TextGray,
                fontSize = 10.sp
            )
        }
    }
}
