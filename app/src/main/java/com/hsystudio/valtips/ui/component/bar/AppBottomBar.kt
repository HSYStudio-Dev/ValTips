package com.hsystudio.valtips.ui.component.bar

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hsystudio.valtips.navigation.Route
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.TextWhite

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    val barBackground = Color(0xFF3F4447)
    val unSelected = Color(0xFF707579)

    NavigationBar(
        containerColor = barBackground,
        tonalElevation = 0.dp
    ) {
        BottomNavItems.forEach { item ->
            val selected = item.route == currentRoute
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onTabSelected(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextWhite,
                    selectedTextColor = TextWhite,
                    unselectedIconColor = unSelected,
                    unselectedTextColor = unSelected,
                    indicatorColor = ColorRed
                )
            )
        }
    }
}

@Preview
@Composable
fun AppBottomBarPreview() {
    AppBottomBar(
        currentRoute = Route.HOME,
        onTabSelected = {}
    )
}
