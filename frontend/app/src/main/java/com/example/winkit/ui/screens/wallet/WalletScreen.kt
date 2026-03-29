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

// --- DATA MODELS ---
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

@Composable
fun WalletScreen() {
    // --- REAL-TIME STATE FOR HACKATHON DEMO ---
    var walletBalance by remember { mutableStateOf(620) }
    var totalEarnings by remember { mutableStateOf(1320) }
    var totalPayouts by remember { mutableStateOf(0) }

    // Initial dummy data
    val transactions = remember {
        mutableStateListOf(
            Transaction(title = "Delivery Earning (5km)", type = "EARNING", amount = 120, isPositive = true, timestamp = "20 Mar at 09:00 PM", icon = Icons.Default.ArrowOutward, iconBgColor = Color(0xFFE8F5E9), iconTintColor = Color(0xFF4CAF50)),
            Transaction(title = "Paid relocation to alternate dark store", type = "RELOCATION PAYOUT", amount = 113, isPositive = true, timestamp = "20 Mar at 09:00 PM", icon = Icons.Default.NearMe, iconBgColor = Color(0xFFF3E5F5), iconTintColor = Color(0xFF9C27B0)),
            Transaction(title = "Weekly Premium", type = "PREMIUM DEDUCTION", amount = 49, isPositive = false, timestamp = "20 Mar at 09:00 PM", icon = Icons.Default.Security, iconBgColor = Color(0xFFFFEBEE), iconTintColor = Color(0xFFF44336))
        )
    }

    // Helper to get current time
    val getCurrentTime = { SimpleDateFormat("dd MMM 'at' hh:mm a", Locale.getDefault()).format(Date()) }

    Scaffold(
        bottomBar = { FakeBottomNavigationBar() },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 1. TOP BLUE WALLET CARD
            item {
                WalletBalanceCard(walletBalance, totalEarnings, totalPayouts)
            }

            // 2. SIMULATION ACTIONS (HACKATHON GOLD!)
            item {
                SimulationActionsCard(
                    onAddEarning = {
                        walletBalance += 150; totalEarnings += 150
                        transactions.add(0, Transaction(title = "Surge Gig Earning", type = "EARNING", amount = 150, isPositive = true, timestamp = getCurrentTime(), icon = Icons.Default.ArrowOutward, iconBgColor = Color(0xFFE8F5E9), iconTintColor = Color(0xFF4CAF50)))
                    },
                    onDeductPremium = {
                        walletBalance -= 49
                        transactions.add(0, Transaction(title = "Weekly Premium", type = "PREMIUM DEDUCTION", amount = 49, isPositive = false, timestamp = getCurrentTime(), icon = Icons.Default.Security, iconBgColor = Color(0xFFFFEBEE), iconTintColor = Color(0xFFF44336)))
                    },
                    onWeatherPayout = {
                        walletBalance += 720; totalPayouts += 720
                        transactions.add(0, Transaction(title = "Heavy Rainfall Coverage", type = "INSURANCE PAYOUT", amount = 720, isPositive = true, timestamp = getCurrentTime(), icon = Icons.Default.WaterDrop, iconBgColor = Color(0xFFE3F2FD), iconTintColor = Color(0xFF2196F3)))
                    },
                    onRelocationPayout = {
                        walletBalance += 113; totalPayouts += 113
                        transactions.add(0, Transaction(title = "Paid relocation to alternate store", type = "RELOCATION PAYOUT", amount = 113, isPositive = true, timestamp = getCurrentTime(), icon = Icons.Default.NearMe, iconBgColor = Color(0xFFF3E5F5), iconTintColor = Color(0xFF9C27B0)))
                    }
                )
            }

            // 3. TRANSACTION HISTORY HEADER
            item {
                Text("Transaction History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), modifier = Modifier.padding(top = 8.dp))
            }

            // 4. TRANSACTION LIST
            items(transactions, key = { it.id }) { tx ->
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

@Composable
fun FakeBottomNavigationBar() {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, contentDescription = null) }, label = { Text("Home") }, selected = false, onClick = { })
        NavigationBarItem(icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) }, label = { Text("Wallet") }, selected = true, onClick = { }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF074768), selectedTextColor = Color(0xFF074768), indicatorColor = Color(0xFFE3F2FD)))
        NavigationBarItem(icon = { Icon(Icons.Default.Person, contentDescription = null) }, label = { Text("Profile") }, selected = false, onClick = { })
    }
}
