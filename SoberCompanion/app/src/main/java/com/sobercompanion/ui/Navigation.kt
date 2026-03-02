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

/**
 * 앱의 모든 화면 라우트를 정의하는 sealed class.
 *
 * sealed class를 사용해 라우트 문자열 오타를 컴파일 타임에 방지합니다.
 * 새 화면을 추가할 때는 여기에 data object를 추가하고
 * SoberCompanionNavHost에 composable 블록을 추가합니다.
 */
sealed class Screen(val route: String) {
    /** 최초 실행 시 이름과 목표를 입력하는 온보딩 화면 */
    data object Onboarding : Screen("onboarding")

    /** 금주 일수와 오늘 상태를 표시하는 메인 대시보드 */
    data object Home : Screen("home")

    /** 기분/욕구/음주 여부를 기록하는 일일 로그 화면 */
    data object DailyLog : Screen("daily_log")

    /** 7일 트렌드 차트와 통계를 보여주는 화면 */
    data object Statistics : Screen("statistics")

    /** 1·3·7·14·30·60·90·180·365일 마일스톤 달성 현황 */
    data object Milestones : Screen("milestones")

    /** 이름, 알림, 앱 정보를 설정하는 화면 */
    data object Settings : Screen("settings")
}

/**
 * 앱 전체 네비게이션 그래프를 설정하는 Composable.
 *
 * 온보딩 완료 여부(isOnboardingCompleted)에 따라 시작 화면을 결정합니다:
 * - 최초 실행 또는 미완료: OnboardingScreen
 * - 기존 사용자: HomeScreen
 *
 * isOnboardingCompleted가 null(로딩 중)일 때는 아무것도 표시하지 않아
 * 깜빡임 없이 올바른 화면으로 이동합니다.
 *
 * @param navController 외부에서 주입 가능하지만 대부분 기본값 사용
 */
@Composable
fun SoberCompanionNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val userPreferencesRepository = UserPreferencesRepository(context)

    // null = 로딩 중, true = 완료, false = 미완료
    val isOnboardingCompleted by userPreferencesRepository.isOnboardingCompleted.collectAsState(initial = null)

    // 온보딩 완료 여부가 확인될 때까지 화면 표시 대기 (깜빡임 방지)
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
        // 온보딩: 완료 후 홈으로 이동하고 백스택에서 제거 (뒤로가기로 돌아올 수 없음)
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
            HomeScreen()
        }

        // 이하 화면들은 모두 popBackStack()으로 홈으로 돌아갑니다
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
