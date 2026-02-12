package com.hsystudio.valtips.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ValTipsColorScheme = darkColorScheme(
    primary = ColorBlack,
    onPrimary = TextGray,

    background = ColorBG,
    onBackground = TextGray,

    surface = ColorBlack,
    onSurface = TextGray
)

@Composable
fun ValTipsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ValTipsColorScheme,
        typography = ValTipsTypography,
        content = content
    )
}
