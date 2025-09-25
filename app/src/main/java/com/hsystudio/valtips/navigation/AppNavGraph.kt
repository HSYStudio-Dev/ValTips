package com.hsystudio.valtips.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hsystudio.valtips.feature.login.SplashScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.SPLASH
    ) {
        composable(Route.SPLASH) {
            // Splash
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
    }
}
