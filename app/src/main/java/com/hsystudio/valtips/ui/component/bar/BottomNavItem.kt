package com.hsystudio.valtips.ui.component.bar

import androidx.annotation.DrawableRes
import com.hsystudio.valtips.R
import com.hsystudio.valtips.navigation.Route

data class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int
)

val BottomNavItems = listOf(
    BottomNavItem(Route.HOME, "전적", R.drawable.ic_home),
    BottomNavItem(Route.AGENT, "요원", R.drawable.ic_agent),
    BottomNavItem(Route.MAP, "맵", R.drawable.ic_map),
    BottomNavItem(Route.SETTING, "설정", R.drawable.ic_setting)
)
