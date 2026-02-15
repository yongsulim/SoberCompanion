package com.sobercompanion.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sobercompanion.data.datastore.UserPreferencesRepository
import com.sobercompanion.ui.screens.DailyLogScreen
import com.sobercompanion.ui.screens.HomeScreen
import com.sobercompanion.ui.screens.MilestonesScreen
import com.sobercompanion.ui.screens.OnboardingScreen
import com.sobercompanion.ui.screens.SettingsScreen
import com.sobercompanion.ui.screens.StatisticsScreen

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
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
    val context = LocalContext.current
    val userPreferencesRepository = UserPreferencesRepository(context)
    val isOnboardingCompleted by userPreferencesRepository.isOnboardingCompleted.collectAsState(initial = null)

    if (isOnboardingCompleted == null) {
        return
    }

    val startDestination = if (isOnboardingCompleted == true) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDailyLog = { navController.navigate(Screen.DailyLog.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onNavigateToMilestones = { navController.navigate(Screen.Milestones.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
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
