package com.example.winkit.ui.navigation

// ─────────────────────────────────────────────────────────────────────────────
// FILE: com/example/winkit/ui/navigation/AppNavigation.kt
// FOLDER: frontend/app/src/main/java/com/example/winkit/ui/navigation/
// CHANGE: Added "language" as the first route. All other routes unchanged.
// ─────────────────────────────────────────────────────────────────────────────

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.winkit.ui.screens.alerts.RelocationAlertModal
import com.example.winkit.ui.screens.checkout.PolicyCheckoutScreen
import com.example.winkit.ui.screens.dashboard.ShiftSafeDashboard
import com.example.winkit.ui.screens.onboarding.IntegrationScreen
import com.example.winkit.ui.screens.onboarding.LanguageSelectionScreen   // ← NEW
import com.example.winkit.ui.screens.onboarding.LoginScreen
import com.example.winkit.ui.screens.onboarding.ScheduleScreen
import com.example.winkit.ui.screens.wallet.WalletScreen
import com.example.winkit.ui.screens.wallet.WalletViewModel
import com.example.winkit.ui.screens.dashboard.DashboardViewModel

@Composable
fun AppNavigation(isLoggedIn: Boolean, sharedPref: SharedPreferences) {
    val navController = rememberNavController()
    val walletViewModel: WalletViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()

    // ── ALWAYS start at the language selector for demo ──────────────────────
    // In production, check sharedPref for "savedLanguage" and skip if already set.
    val startDest = "language"

    NavHost(navController = navController, startDestination = startDest) {

        // ── SCREEN 0: LANGUAGE SELECTION (NEW) ──────────────────────────────
        composable("language") {
            LanguageSelectionScreen(
                onLanguageSelected = {
                    // Navigate to login, removing language from backstack so
                    // pressing back on login exits the app rather than re-showing language picker
                    navController.navigate("login") {
                        popUpTo("language") { inclusive = true }
                    }
                }
            )
        }

        // ── SCREEN 1: LOGIN ──────────────────────────────────────────────────
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("integration")
                }
            )
        }

        // ── SCREEN 2: INTEGRATION ────────────────────────────────────────────
        composable("integration") {
            IntegrationScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate("schedule") }
            )
        }

        // ── SCREEN 3: SCHEDULE ───────────────────────────────────────────────
        composable("schedule") {
            ScheduleScreen(
                onBack = { navController.popBackStack() },
                onFinish = {
                    navController.navigate("checkout")
                }
            )
        }

        // ── SCREEN 4: CHECKOUT ───────────────────────────────────────────────
        composable("checkout") {
            PolicyCheckoutScreen(
                onBack = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // ── SCREEN 5: DASHBOARD ──────────────────────────────────────────────
        composable("dashboard") {
            ShiftSafeDashboard(
                viewModel = dashboardViewModel,
                navController = navController,
                onTriggerAlert = { navController.navigate("alert") }
            )
        }

        // ── SCREEN 6: WALLET ─────────────────────────────────────────────────
        composable("wallet") {
            WalletScreen(navController = navController, viewModel = walletViewModel)
        }

        // ── SCREEN 7: DISASTER ALERT ─────────────────────────────────────────
        composable("alert") {
            RelocationAlertModal(
                onAccept = { navController.popBackStack() }
            )
        }
    }
}
