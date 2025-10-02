package com.hsystudio.valtips.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hsystudio.valtips.feature.login.ui.LoginScreen
import com.hsystudio.valtips.feature.login.ui.OnboardingScreen
import com.hsystudio.valtips.feature.login.ui.SplashScreen
import com.hsystudio.valtips.ui.component.bar.AppBottomBar
import com.hsystudio.valtips.ui.component.bar.BottomNavItems

@Composable
fun AppNavGraph(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in BottomNavItems.map { it.route }) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(Route.HOME) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash
            composable(Route.SPLASH) {
                SplashScreen(
                    onNavigateToOnBoarding = {
                        navController.navigate(Route.ONBOARDING) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Route.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Route.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            // Onboarding
            composable(Route.ONBOARDING) {
                OnboardingScreen(
                    onStart = {
                        navController.navigate(Route.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            // Login
            composable(Route.LOGIN) {
                LoginScreen(
                    onNavigateToHome = {
                        navController.navigate(Route.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Home(전적)
            composable(Route.HOME) {
            }

            // Agent
            composable(Route.AGENT) {
            }

            // Map
            composable(Route.MAP) {
            }

            // Setting
            composable(Route.SETTING) {
            }
        }
    }
}
