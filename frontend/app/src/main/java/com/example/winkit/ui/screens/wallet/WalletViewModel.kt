package com.example.winkit.ui.screens.wallet

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// Moved the data class here so the ViewModel can manage it
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: String,
    val amount: Int,
    val isPositive: Boolean,
    val timestamp: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTintColor: Color
)

class WalletViewModel : ViewModel() {
    // 1. Holding the state globally
    var walletBalance by mutableStateOf(620)
        private set
    var totalEarnings by mutableStateOf(1320)
        private set
    var totalPayouts by mutableStateOf(0)
        private set

    val transactions = mutableStateListOf(
        Transaction(title = "Delivery Earning (5km)", type = "EARNING", amount = 120, isPositive = true, timestamp = "20 Mar at 09:00 PM", icon = Icons.Default.ArrowOutward, iconBgColor = Color(0xFFE8F5E9), iconTintColor = Color(0xFF4CAF50)),
        Transaction(title = "Paid relocation to alternate dark store", type = "RELOCATION PAYOUT", amount = 113, isPositive = true, timestamp = "20 Mar at 09:00 PM", icon = Icons.Default.NearMe, iconBgColor = Color(0xFFF3E5F5), iconTintColor = Color(0xFF9C27B0)),
        Transaction(title = "Weekly Premium", type = "PREMIUM DEDUCTION", amount = 49, isPositive = false, timestamp = "20 Mar at 09:00 PM", icon = Icons.Default.Security, iconBgColor = Color(0xFFFFEBEE), iconTintColor = Color(0xFFF44336))
    )

    private fun getCurrentTime(): String {
        return SimpleDateFormat("dd MMM 'at' hh:mm a", Locale.getDefault()).format(Date())
    }

    // 2. These functions act as the bridge. 
    // Right now they just do math. Later, your AI Backend API calls go right inside here!
    
    fun simulateGigEarning(amount: Int = 150) {
        walletBalance += amount
        totalEarnings += amount
        transactions.add(0, Transaction(title = "Surge Gig Earning", type = "EARNING", amount = amount, isPositive = true, timestamp = getCurrentTime(), icon = Icons.Default.ArrowOutward, iconBgColor = Color(0xFFE8F5E9), iconTintColor = Color(0xFF4CAF50)))
    }

    fun deductPremium(amount: Int = 49) {
        walletBalance -= amount
        transactions.add(0, Transaction(title = "Weekly Premium", type = "PREMIUM DEDUCTION", amount = amount, isPositive = false, timestamp = getCurrentTime(), icon = Icons.Default.Security, iconBgColor = Color(0xFFFFEBEE), iconTintColor = Color(0xFFF44336)))
    }

    fun simulateWeatherPayout(amount: Int = 720) {
        walletBalance += amount
        totalPayouts += amount
        transactions.add(0, Transaction(title = "Heavy Rainfall Coverage", type = "INSURANCE PAYOUT", amount = amount, isPositive = true, timestamp = getCurrentTime(), icon = Icons.Default.WaterDrop, iconBgColor = Color(0xFFE3F2FD), iconTintColor = Color(0xFF2196F3)))
    }

    fun simulateRelocationPayout(amount: Int = 113) {
        walletBalance += amount
        totalPayouts += amount
        transactions.add(0, Transaction(title = "Paid relocation to alternate store", type = "RELOCATION PAYOUT", amount = amount, isPositive = true, timestamp = getCurrentTime(), icon = Icons.Default.NearMe, iconBgColor = Color(0xFFF3E5F5), iconTintColor = Color(0xFF9C27B0)))
    }
}
