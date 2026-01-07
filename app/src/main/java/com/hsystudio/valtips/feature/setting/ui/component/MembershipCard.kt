package com.hsystudio.valtips.feature.setting.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.R
import com.hsystudio.valtips.ui.component.DefaultButton
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.GradientBlack

@Composable
fun MembershipCard(
    isProMember: Boolean,
    nextBillingDate: String?,
    onManageClick: () -> Unit
) {
    // 배경 그라데이션
    val bgBrush = if (isProMember) {
        // 골드
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFECC440),
                Color(0xFFDDAC17),
                Color(0xFFFFFF95)
            )
        )
    } else {
        // 그레이
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF97989A),
                Color(0xFF8A8B8F),
                Color(0xFFDADBDD)
            )
        )
    }
    val borderColor = if (isProMember) Color(0xFFDDAC17) else Color(0xFF8A8B8F)

    // 텍스트
    val title = if (isProMember) "프로 멤버십 활성화 중" else "프로 멤버십 비활성화"
    val nextBilling = nextBillingDate ?: "-"

    // 아이콘 리소스
    val iconAdResId = R.drawable.ic_membership_ad
    val iconMultiResId = R.drawable.ic_membership_multi

    // 흑백 필터 설정
    val colorMatrix = remember(isProMember) {
        ColorMatrix().apply {
            setToSaturation(if (isProMember) 1f else 0f)
        }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 좌측 텍스트 영역
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "다음 결제일: $nextBilling",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // 우측 아이콘 영역
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // 광고 제거 아이콘
                        Image(
                            painter = painterResource(id = iconAdResId),
                            contentDescription = "광고 제거 기능",
                            modifier = Modifier.size(36.dp),
                            colorFilter = ColorFilter.colorMatrix(colorMatrix),
                            contentScale = ContentScale.Fit
                        )
                        // 다중 계정 아이콘
                        Image(
                            painter = painterResource(id = iconMultiResId),
                            contentDescription = "다중 계정 기능",
                            modifier = Modifier.size(36.dp),
                            colorFilter = ColorFilter.colorMatrix(colorMatrix),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // 하단 멤버십 관리 버튼
                DefaultButton(
                    text = if (isProMember) "구독 관리" else "멤버십 업그레이드",
                    startColor = ColorBlack,
                    endColor = GradientBlack,
                    borderColor = Color.Transparent,
                    onClick = onManageClick
                )
            }
        }
    }
}
