package com.example.winkit.ui.screens.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController
import com.example.winkit.ui.components.ShiftSafeBottomNav
import androidx.lifecycle.viewmodel.compose.viewModel

// --- DATA MODELS ---

@Composable
fun WalletScreen(
    navController: NavController, 
    viewModel: WalletViewModel
) {
    
    Scaffold(
        bottomBar = { ShiftSafeBottomNav(navController = navController) },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 1. Pass the ViewModel's state to the Card
            item {
                WalletBalanceCard(viewModel.walletBalance, viewModel.totalEarnings, viewModel.totalPayouts)
            }

            // 2. Point the buttons to the ViewModel functions
            item {
                SimulationActionsCard(
                    onAddEarning = { viewModel.simulateGigEarning() },
                    onDeductPremium = { viewModel.deductPremium() },
                    onWeatherPayout = { viewModel.simulateWeatherPayout() },
                    onRelocationPayout = { viewModel.simulateRelocationPayout() }
                )
            }

            item {
                Text("Transaction History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), modifier = Modifier.padding(top = 8.dp))
            }

            // 3. Read the transactions list directly from the ViewModel
            items(viewModel.transactions, key = { it.id }) { tx ->
                TransactionRow(tx)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
@Composable
fun WalletBalanceCard(balance: Int, earnings: Int, payouts: Int) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF074768), // Deep Blue
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Wallet", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Surface(color = Color.White.copy(alpha = 0.1f), shape = CircleShape) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.padding(8.dp).size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Main Balance
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Wallet Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Text("₹$balance", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sub Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatBox("Total Earnings", "₹$earnings", Modifier.weight(1f))
                StatBox("Total Payouts", "₹$payouts", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Insurance Status Footer
            Surface(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Insurance Status", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ACTIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Weekly Premium", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("₹49/wk", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(title: String, amount: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SimulationActionsCard(onAddEarning: () -> Unit, onDeductPremium: () -> Unit, onWeatherPayout: () -> Unit, onRelocationPayout: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFDE7), // Light yellow tint
        border = BorderStroke(1.dp, Color(0xFFFFF59D)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simulation Actions", color = Color(0xFFF57F17), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SimulationButton("+ Gig Earning", Modifier.weight(1f), onAddEarning)
                SimulationButton("- Deduct Premium", Modifier.weight(1f), onDeductPremium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SimulationButton("+ Weather Payout", Modifier.weight(1f), onWeatherPayout)
                SimulationButton("+ Relocation Payout", Modifier.weight(1f), onRelocationPayout)
            }
        }
    }
}

@Composable
fun SimulationButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFFFE082)),
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text, color = Color(0xFFF57F17), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionRow(tx: Transaction) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier.size(48.dp).background(tx.iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(tx.icon, contentDescription = null, tint = tx.iconTintColor)
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Text(tx.type, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(tx.timestamp, fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Amount
            Text(
                text = "${if (tx.isPositive) "+" else "-"}₹${tx.amount}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = if (tx.isPositive) Color(0xFF00C853) else Color(0xFF424242)
            )
        }
    }
}

