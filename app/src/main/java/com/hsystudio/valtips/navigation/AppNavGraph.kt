package com.hsystudio.valtips.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.hsystudio.valtips.feature.agent.ui.AgentDetailScreen
import com.hsystudio.valtips.feature.agent.ui.AgentsScreen
import com.hsystudio.valtips.feature.lineup.ui.AgentSelectScreen
import com.hsystudio.valtips.feature.lineup.ui.LineupDetailScreen
import com.hsystudio.valtips.feature.lineup.ui.LineupsScreen
import com.hsystudio.valtips.feature.lineup.ui.MapSelectScreen
import com.hsystudio.valtips.feature.login.ui.LoginScreen
import com.hsystudio.valtips.feature.login.ui.OnboardingScreen
import com.hsystudio.valtips.feature.login.ui.SplashScreen
import com.hsystudio.valtips.feature.login.viewmodel.LoginViewModel
import com.hsystudio.valtips.feature.map.ui.MapDetailScreen
import com.hsystudio.valtips.feature.map.ui.MapsScreen
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
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
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

            /** Agents(요원 리스트) */
            composable(Route.AGENT) {
                AgentsScreen(
                    onAgentClick = { agentUuid ->
                        navController.navigate("agent_detail/$agentUuid")
                    }
                )
            }
            /** Agent Detail(요원 상세) */
            composable(
                route = Route.AGENT_DETAIL,
                arguments = listOf(navArgument("agentUuid") { type = NavType.StringType })
            ) {
                AgentDetailScreen(
                    onBack = { navController.popBackStack() },
                    onGuideClick = { agentUuid ->
                        navController.navigate("map_select/$agentUuid")
                    }
                )
            }
            /** Map Select(맵 선택) */
            composable(
                route = Route.MAP_SELECT,
                arguments = listOf(navArgument("agentUuid") { type = NavType.StringType })
            ) {
                MapSelectScreen(
                    onBack = { navController.popBackStack() },
                    onMapClick = { agentUuid, mapUuid ->
                        navController.navigate("lineup/agentUuid=$agentUuid&mapUuid=$mapUuid")
                    }
                )
            }

            /** Maps(맵 리스트) */
            composable(Route.MAP) {
                MapsScreen(
                    onMapClick = { mapUuid ->
                        navController.navigate("map_detail/$mapUuid")
                    }
                )
            }
            /** Map Detail(맵 상세) */
            composable(
                route = Route.MAP_DETAIL,
                arguments = listOf(navArgument("mapUuid") { type = NavType.StringType })
            ) {
                MapDetailScreen(
                    onBack = { navController.popBackStack() },
                    onGuideClick = { mapUuid ->
                        navController.navigate("agent_select/$mapUuid")
                    }
                )
            }
            /** Agent Select(요원 선택) */
            composable(
                route = Route.AGENT_SELECT,
                arguments = listOf(navArgument("mapUuid") { type = NavType.StringType })
            ) {
                AgentSelectScreen(
                    onBack = { navController.popBackStack() },
                    onAgentClick = { agentUuid, mapUuid ->
                        navController.navigate("lineup/agentUuid=$agentUuid&mapUuid=$mapUuid")
                    }
                )
            }

            /** Lineups(라인업 리스트) */
            composable(
                route = Route.LINEUP,
                arguments = listOf(
                    navArgument("agentUuid") { type = NavType.StringType },
                    navArgument("mapUuid") { type = NavType.StringType }
                )
            ) {
                LineupsScreen(
                    onBack = { navController.popBackStack() },
                    onLineupClick = { lineupId ->
                        navController.navigate("lineup_detail/$lineupId")
                    }
                )
            }
            /** Lineup Detail(라인업 상세) */
            composable(
                route = Route.LINEUP_DETAIL,
                arguments = listOf(
                    navArgument("lineupId") { type = NavType.IntType }
                )
            ) {
                LineupDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Setting
            composable(Route.SETTING) {
            }
        }
    }
}
