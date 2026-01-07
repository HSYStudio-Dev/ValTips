package com.hsystudio.valtips.feature.setting.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsystudio.valtips.R
import com.hsystudio.valtips.data.auth.FakeRiotAccount
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.Spoqa
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun RiotAccountCard(
    account: FakeRiotAccount,
    onClick: () -> Unit,
    onLogout: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isActive = account.isActive
    val activityColor = if (isActive) ColorMint else ColorRed

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, activityColor.copy(0.5f)),
        colors = CardDefaults.cardColors(containerColor = ColorBlack),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isActive) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌측 프로필 아이콘
            Image(
                painter = painterResource(id = R.drawable.ic_account_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF666666), RoundedCornerShape(16.dp))
            )

            Spacer(Modifier.width(8.dp))

            // 우측 컨텐츠
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 닉네임 + 태그
                Text(
                    text = buildAnnotatedString {
                        // 닉네임 스타일 적용
                        withStyle(
                            style = SpanStyle(
                                fontFamily = Spoqa,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = TextWhite
                            )
                        ) {
                            append(account.gameName)
                        }

                        // 태그 스타일 적용
                        withStyle(
                            style = SpanStyle(
                                fontFamily = Spoqa,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = TextGray
                            )
                        ) {
                            append("#${account.tagLine}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )

                // 상태 표시
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(activityColor)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = if (isActive) "활성화" else "비활성화",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextWhite
                    )
                }
            }

            // 로그아웃 메뉴(더보기)
            Box {
                IconButton(
                    onClick = { menuExpanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = TextWhite
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("로그아웃") },
                        onClick = {
                            menuExpanded = false
                            onLogout()
                        }
                    )
                }
            }
        }
    }
}
