package com.hsystudio.valtips.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hsystudio.valtips.R

// Spoqa FontFamily
val Spoqa = FontFamily(
    Font(R.font.spoqa_han_sans_neo_regular, FontWeight.Normal),
    Font(R.font.spoqa_han_sans_neo_medium, FontWeight.Medium),
    Font(R.font.spoqa_han_sans_neo_bold, FontWeight.Bold)
)

// Valorant FontFamily
val Valorant = FontFamily(
    Font(R.font.valorant, FontWeight.Normal)
)

// Typography
val ValTipsTypography = Typography(
    // 메인 로고
    displayLarge = TextStyle(
        fontFamily = Valorant,
        fontWeight = FontWeight.Normal,
        fontSize = 56.sp,
        lineHeight = 61.6.sp
    ),
    // TopAppBar 타이틀
    titleLarge = TextStyle(
        fontFamily = Valorant,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 26.4.sp
    ),

    // Headline 1
    headlineLarge = TextStyle(
        fontFamily = Spoqa,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 26.4.sp
    ),
    // Headline 2
    headlineMedium = TextStyle(
        fontFamily = Spoqa,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 22.sp
    ),
    // Headline 3
    headlineSmall = TextStyle(
        fontFamily = Spoqa,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 19.2.sp
    ),

    // Body 1
    bodyLarge = TextStyle(
        fontFamily = Spoqa,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    ),
    // Body 2
    bodyMedium = TextStyle(
        fontFamily = Spoqa,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    ),
    // Body 3
    bodySmall = TextStyle(
        fontFamily = Spoqa,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    ),
    // Body 4
    labelSmall = TextStyle(
        fontFamily = Spoqa,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 14.4.sp
    )
)
