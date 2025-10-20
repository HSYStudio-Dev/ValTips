package com.hsystudio.valtips.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.hsystudio.valtips.feature.login.ui.LoginScreen
import com.hsystudio.valtips.feature.login.ui.OnboardingScreen
import com.hsystudio.valtips.feature.login.ui.SplashScreen
import com.hsystudio.valtips.feature.login.viewmodel.LoginViewModel
import com.hsystudio.valtips.feature.stats.ui.StatsScreen
import com.hsystudio.valtips.ui.component.bar.AppBottomBar
import com.hsystudio.valtips.ui.component.bar.BottomNavItems

@Composable
fun AppNavGraph(
    onExitApp: () -> Unit,
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in BottomNavItems.map { it.route }) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(Route.HOME) { saveState = true }
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
            startDestination = Graph.AUTH,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ───────────────── AUTH GRAPH (Splash → Onboarding → Login) ─────────────────
            navigation(
                startDestination = Route.SPLASH,
                route = Graph.AUTH
            ) {
                /** Splash */
                composable(Route.SPLASH) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Graph.AUTH)
                    }
                    val vm: LoginViewModel = hiltViewModel(parentEntry)

                    SplashScreen(
                        onNavigateToOnBoarding = {
                            navController.navigate(Route.ONBOARDING) {
                                popUpTo(Route.SPLASH) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate(Route.LOGIN) {
                                popUpTo(Route.SPLASH) { inclusive = true }
                            }
                        },
                        onNavigateToHome = {
                            navController.navigate(Route.HOME) {
                                popUpTo(Graph.AUTH) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onExitApp = onExitApp,
                        viewModel = vm
                    )
                }

                /** Onboarding */
                composable(Route.ONBOARDING) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Graph.AUTH)
                    }
                    val vm: LoginViewModel = hiltViewModel(parentEntry)

                    OnboardingScreen(
                        onStart = {
                            navController.navigate(Route.LOGIN) {
                                popUpTo(Route.ONBOARDING) { inclusive = true }
                            }
                        },
                        viewModel = vm
                    )
                }

                /** Login */
                composable(Route.LOGIN) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Graph.AUTH)
                    }
                    val vm: LoginViewModel = hiltViewModel(parentEntry)

                    LoginScreen(
                        onNavigateToHome = {
                            navController.navigate(Route.HOME) {
                                popUpTo(Graph.AUTH) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        viewModel = vm
                    )
                }
            }

            // Home(전적)
            composable(Route.HOME) {
                StatsScreen()
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
