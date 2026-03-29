package com.example.winkit.ui.navigation

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.winkit.domain.models.DashboardState
import com.example.winkit.domain.models.EnvironmentType
import com.example.winkit.ui.screens.alerts.RelocationAlertModal
import com.example.winkit.ui.screens.dashboard.ShiftSafeDashboard
import com.example.winkit.ui.screens.onboarding.IntegrationScreen
import com.example.winkit.ui.screens.onboarding.LoginScreen
import com.example.winkit.ui.screens.onboarding.ScheduleScreen
import com.example.winkit.ui.screens.wallet.WalletScreen

@Composable
fun AppNavigation(isLoggedIn: Boolean, sharedPref: SharedPreferences) {
    val navController = rememberNavController()

    // 🔴 HARDCODED FOR DEMO: Always boot to the login screen!
    val startDest = "login"

    NavHost(navController = navController, startDestination = startDest) {
        
        // --- SCREEN 1: LOGIN ---
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("integration")
                }
            )
        }

        // --- SCREEN 2: INTEGRATION ---
        composable("integration") {
            IntegrationScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate("schedule") }
            )
        }
        
        // --- SCREEN 3: SCHEDULE ---
        composable("schedule") {
            ScheduleScreen(
                onBack = { navController.popBackStack() },
                onFinish = {
                    // 🔴 DISABLED FOR DEMO: We aren't saving the login state
                    // sharedPref.edit().putBoolean("isLoggedIn", true).apply()
                    
                    navController.navigate("dashboard") {
                        // Clear the backstack so pressing "back" exits the app instead of going to login
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- SCREEN 5: DASHBOARD ---
        composable("dashboard") {
            val mockState = DashboardState(
                environment = EnvironmentType.CLEAR_DAY,
                wetBulbTemp = 24,
                aqi = 85,
                storeStatus = "ONLINE"
            )

            ShiftSafeDashboard(
                state = mockState,
                navController = navController, // <--- Pass it here
                onTriggerAlert = { navController.navigate("alert") }
            )
        }
        composable("wallet") {
            WalletScreen(navController = navController) // <--- Pass it here
        }
        // --- SCREEN 7: DISASTER ALERT ---
        composable("alert") {
            RelocationAlertModal(
                onAccept = { navController.popBackStack() }
            )
        }
    }
}
