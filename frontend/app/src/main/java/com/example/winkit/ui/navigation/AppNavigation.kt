package com.example.winkit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.winkit.domain.models.DashboardState
import com.example.winkit.domain.models.EnvironmentType
import com.example.winkit.ui.screens.dashboard.ShiftSafeDashboard
import com.example.winkit.ui.screens.alerts.RelocationAlertModal

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // We start at the dashboard for testing purposes right now
    NavHost(navController = navController, startDestination = "dashboard") {
        
        composable("dashboard") {
            // Mock data for your pitch demo
            val mockState = DashboardState(
                environment = EnvironmentType.HEATWAVE, // Swap this to RAIN or SMOG to test!
                wetBulbTemp = 34,
                aqi = 160,
                storeStatus = "ONLINE"
            )
            
            ShiftSafeDashboard(
                state = mockState,
                // We pass a trigger function so a button on the dashboard can open the alert
                onTriggerAlert = { navController.navigate("alert") } 
            )
        }
        
        composable("alert") {
            RelocationAlertModal(
                onAccept = { 
                    // When they swipe to accept, pop the modal off the stack to go back
                    navController.popBackStack() 
                }
            )
        }
    }
}
