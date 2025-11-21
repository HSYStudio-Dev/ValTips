package com.hsystudio.valtips.feature.map.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsystudio.valtips.feature.map.model.MapDetailUi
import com.hsystudio.valtips.ui.theme.Spoqa
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite
import com.hsystudio.valtips.ui.theme.Valorant

@Composable
fun MapInfoSection(
    data: MapDetailUi
) {
    val eng = data.englishName.orEmpty()
    val kor = data.displayName
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 맵 이름
        Text(
            text = buildAnnotatedString {
                if (eng.isNotBlank()) {
                    // 영문
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Valorant,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,
                            color = TextWhite
                        )
                    ) { append(eng) }
                    // 경계선
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Spoqa,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = TextWhite
                        )
                    ) { append(" | ") }
                }
                // 한글
                withStyle(
                    style = SpanStyle(
                        fontFamily = Spoqa,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = TextWhite
                    )
                ) { append(kor) }
            }
        )
        // 사이트 안내
        data.tacticalDescription?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineSmall,
                color = TextGray
            )
        }
    }
}
