package com.sobercompanion.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sobercompanion.ui.screens.DailyLogScreen
import com.sobercompanion.ui.screens.HomeScreen
import com.sobercompanion.ui.screens.MilestonesScreen
import com.sobercompanion.ui.screens.SettingsScreen
import com.sobercompanion.ui.screens.StatisticsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object DailyLog : Screen("daily_log")
    data object Statistics : Screen("statistics")
    data object Milestones : Screen("milestones")
    data object Settings : Screen("settings")
}

@Composable
fun SoberCompanionNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.DailyLog.route) {
            DailyLogScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Milestones.route) {
            MilestonesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
