package com.hsystudio.valtips.feature.setting.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsystudio.valtips.R
import com.hsystudio.valtips.data.auth.FakeRiotAccount
import com.hsystudio.valtips.ui.component.DefaultButton
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.ColorStroke
import com.hsystudio.valtips.ui.theme.GradientRed
import com.hsystudio.valtips.ui.theme.Spoqa
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun CurrentAccountCard(
    isLoggedIn: Boolean,
    currentAccount: FakeRiotAccount?,
    onLoginClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ColorStroke),
        colors = CardDefaults.cardColors(containerColor = ColorBlack),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌측 프로필 아이콘
            Image(
                painter = painterResource(id = R.drawable.ic_account_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF666666), RoundedCornerShape(16.dp))
            )

            Spacer(Modifier.width(16.dp))

            // 우측 컨텐츠
            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (!isLoggedIn || currentAccount == null) {
                    // 로그인 X
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 안내 문구
                        Text(
                            text = "로그인이 필요합니다",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextWhite,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        // 로그인 버튼
                        DefaultButton(
                            text = "Riot ID 로그인",
                            startColor = ColorRed,
                            endColor = GradientRed,
                            borderColor = ColorRed,
                            onClick = onLoginClick,
                        )
                    }
                } else {
                    // 로그인 O
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // 닉네임 + 태그
                        Text(
                            text = buildAnnotatedString {
                                // 닉네임 스타일 적용
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = Spoqa,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp,
                                        color = TextWhite
                                    )
                                ) {
                                    append(currentAccount.gameName)
                                }

                                // 태그 스타일 적용
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = Spoqa,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp,
                                        color = TextGray
                                    )
                                ) {
                                    append("#${currentAccount.tagLine}")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = MaterialTheme.typography.headlineMedium.lineHeight
                        )

                        // 레벨
                        Text(
                            text = "Lv. ${currentAccount.level}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhite
                        )

                        // 상태 표시
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(ColorMint)
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = "접속중",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextWhite
                            )
                        }
                    }
                }
            }
        }
    }
}
