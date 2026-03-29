package com.example.winkit.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun ShiftSafeBottomNav(navController: NavController) {
    // This allows the nav bar to know which screen we are currently on
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        
        // HOME / DASHBOARD TAB
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "dashboard",
            onClick = {
                if (currentRoute != "dashboard") {
                    navController.navigate("dashboard") {
                        // Pop up to dashboard to avoid huge backstacks
                        popUpTo("dashboard") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF074768),
                selectedTextColor = Color(0xFF074768),
                indicatorColor = Color(0xFFE3F2FD)
            )
        )

        // WALLET TAB
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
            label = { Text("Wallet") },
            selected = currentRoute == "wallet",
            onClick = {
                if (currentRoute != "wallet") {
                    navController.navigate("wallet") {
                        popUpTo("dashboard") // Keep dashboard as the root
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF074768),
                selectedTextColor = Color(0xFF074768),
                indicatorColor = Color(0xFFE3F2FD)
            )
        )

        // PROFILE TAB (Placeholder for demo)
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = { /* Do nothing for the pitch video to keep it focused */ }
        )
    }
}
