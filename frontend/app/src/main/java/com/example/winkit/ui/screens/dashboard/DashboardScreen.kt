package com.example.winkit.ui.screens.dashboard

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.winkit.domain.models.DashboardState
import com.example.winkit.domain.models.EnvironmentType

// ── Colour tokens matching the screenshot ──────────────────────────────────
private val BannerStart  = Color(0xFF5B2D8E)   // deep purple (left)
private val BannerEnd    = Color(0xFF8B3FBF)   // violet (right)
private val StarDot      = Color(0xCCFFFFFF)
private val NeonGreen    = Color(0xFF00E5A0)
private val TagModerate  = Color(0xFFFF8C42)   // orange text
private val TagLow       = Color(0xFF00C48C)   // teal text
private val TagPoor      = Color(0xFFE53935)   // red text
private val TagOpen      = Color(0xFF00ACC1)   // cyan text
private val CardBg       = Color(0xFFFFFFFF)
private val PageBg       = Color(0xFFF3F4F8)
private val TextDark     = Color(0xFF1A1A2E)
private val TextGray     = Color(0xFF8E8E9A)
private val NavSelected  = Color(0xFF5B2D8E)
private val NavUnselected= Color(0xFFB0B0C0)
private val GpsBg        = Color(0xFFF8F9FF)
private val GpsIcon      = Color(0xFF5B2D8E)

// ── Root composable (replaces ShiftSafeDashboard) ─────────────────────────
@Composable
fun ShiftSafeDashboard(state: DashboardState, onTriggerAlert: () -> Unit) {
    Scaffold(
        bottomBar = { WinkitBottomNav() },
        containerColor = PageBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── 1. Purple weather banner ──────────────────────────────────
            WeatherBanner(state)

            Spacer(modifier = Modifier.height(20.dp))

            // ── 2. Live Risk Metrics ──────────────────────────────────────
            Text(
                text  = "Live Risk Metrics",
                color = TextDark,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            RiskMetricsGrid(state)

            Spacer(modifier = Modifier.height(20.dp))

            // ── 3. Live GPS Tracking ──────────────────────────────────────
            GpsTrackingSection()

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ── Weather banner ─────────────────────────────────────────────────────────
@Composable
fun WeatherBanner(state: DashboardState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(BannerStart, BannerEnd)
                ),
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
    ) {
        // Scattered star dots (static decorative layer)
        StarField()

        // Right side: profile avatar circle
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A6B6B))
        )

        // Left: text content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp, top = 12.dp)
        ) {
            Text(
                text       = "Welcome back,",
                color      = Color.White.copy(alpha = 0.85f),
                fontSize   = 13.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text       = "Rahul Kumar",          // keep static — wire to state if needed
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint               = Color.White.copy(alpha = 0.8f),
                    modifier           = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text     = "11:45 PM",
                    color    = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text       = "${state.wetBulbTemp}°C",
                color      = Color.White,
                fontSize   = 52.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 56.sp
            )
            Text(
                text       = "Cloudy",
                color      = Color.White.copy(alpha = 0.9f),
                fontSize   = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Right side lower: cloudy moon illustration placeholder + coverage pill
        Column(
            modifier           = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp, top = 40.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Moon + cloud shape (simplified with overlapping circles)
            MoonCloudIllustration()

            Spacer(modifier = Modifier.height(10.dp))

            // "Coverage • Active" pill
            CoveragePill()
        }
    }
}

// ── Decorative star field ──────────────────────────────────────────────────
@Composable
fun StarField() {
    // Static star positions approximated from the screenshot
    Box(modifier = Modifier.fillMaxSize()) {
        val dots = listOf(
            0.15f to 0.08f, 0.7f to 0.05f, 0.85f to 0.15f,
            0.05f to 0.5f,  0.6f to 0.35f, 0.9f to 0.55f,
            0.4f to 0.72f,  0.12f to 0.82f,0.75f to 0.8f,
            0.52f to 0.9f,  0.30f to 0.20f,0.95f to 0.38f
        )
        dots.forEach { (xFrac, yFrac) ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
            ) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (xFrac * 300).dp,
                            y = (yFrac * 240).dp
                        )
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(StarDot)
                )
            }
        }
    }
}

// ── Moon + cloud illustration ──────────────────────────────────────────────
@Composable
fun MoonCloudIllustration() {
    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 70.dp)
    ) {
        // Moon circle (large white glowing)
        Box(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.88f))
        )
        // Cloud body (wide ellipse at bottom)
        Box(
            modifier = Modifier
                .width(90.dp)
                .height(32.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.55f))
        )
    }
}

// ── Coverage active pill ───────────────────────────────────────────────────
@Composable
fun CoveragePill() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(NeonGreen)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text       = "Coverage",
                color      = Color.White.copy(alpha = 0.8f),
                fontSize   = 10.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text       = "Active",
                color      = NeonGreen,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── 2×2 Risk Metrics Grid ─────────────────────────────────────────────────
@Composable
fun RiskMetricsGrid(state: DashboardState) {
    val wetBulbRisk = if (state.wetBulbTemp > 30) "HIGH" else "MODERATE"
    val wetBulbColor = if (state.wetBulbTemp > 30) TagPoor else TagModerate
    val aqiRisk  = if (state.aqi > 300) "POOR" else if (state.aqi > 150) "MODERATE" else "GOOD"
    val aqiColor = if (state.aqi > 300) TagPoor else if (state.aqi > 150) TagModerate else TagLow

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Wet Bulb Temp card
            RiskCard(
                modifier  = Modifier.weight(1f),
                iconVector = Icons.Default.Thermostat,
                iconTint  = Color(0xFFFF7043),
                tag       = wetBulbRisk,
                tagColor  = wetBulbColor,
                value     = "${state.wetBulbTemp}°C",
                label     = "Wet Bulb Temp"
            )
            // Rain Probability card
            RiskCard(
                modifier  = Modifier.weight(1f),
                iconVector = Icons.Default.WaterDrop,
                iconTint  = Color(0xFF5C9EE8),
                tag       = "LOW",
                tagColor  = TagLow,
                value     = "10%",
                label     = "Rain Probability"
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Air Quality card
            RiskCard(
                modifier  = Modifier.weight(1f),
                iconVector = Icons.Default.Air,
                iconTint  = Color(0xFF78909C),
                tag       = aqiRisk,
                tagColor  = aqiColor,
                value     = "${state.aqi}",
                label     = "Air Quality Index"
            )
            // Primary Hub card
            RiskCard(
                modifier  = Modifier.weight(1f),
                iconVector = Icons.Default.Store,
                iconTint  = Color(0xFF26A69A),
                tag       = state.storeStatus.uppercase(),
                tagColor  = TagOpen,
                value     = "potheri",       // wire to state.hubName if available
                label     = "Primary Hub"
            )
        }
    }
}

// ── Single risk metric card ────────────────────────────────────────────────
@Composable
fun RiskCard(
    modifier   : Modifier,
    iconVector : ImageVector,
    iconTint   : Color,
    tag        : String,
    tagColor   : Color,
    value      : String,
    label      : String
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment   = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = iconVector,
                    contentDescription = null,
                    tint               = iconTint,
                    modifier           = Modifier.size(22.dp)
                )
                // Tag badge
                Text(
                    text       = tag,
                    color      = tagColor,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(tagColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text       = value,
                color      = TextDark,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text     = label,
                color    = TextGray,
                fontSize = 12.sp
            )
        }
    }
}

// ── GPS Tracking section ───────────────────────────────────────────────────
@Composable
fun GpsTrackingSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Header row
        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment   = Alignment.CenterVertically
        ) {
            Text(
                text       = "Live GPS Tracking",
                color      = TextDark,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
            // Active badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(NeonGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = Icons.Default.MyLocation,
                    contentDescription = null,
                    tint               = NeonGreen,
                    modifier           = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text       = "Active",
                    color      = NeonGreen,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Destination card
        Card(
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier  = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GpsIcon.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint               = GpsIcon,
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text       = "DELIVERY DESTINATION",
                            color      = TextGray,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text       = "Tap to set destination",
                            color      = TextDark,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Text(
                    text       = "Edit",
                    color      = NavSelected,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Bottom Navigation ──────────────────────────────────────────────────────
@Composable
fun WinkitBottomNav() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick  = {},
            icon     = {
                Icon(Icons.Default.Home, contentDescription = "Home")
            },
            label    = { Text("HOME", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = NavSelected,
                selectedTextColor   = NavSelected,
                unselectedIconColor = NavUnselected,
                unselectedTextColor = NavUnselected,
                indicatorColor      = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick  = {},
            icon     = {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet")
            },
            label    = { Text("WALLET", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = NavSelected,
                selectedTextColor   = NavSelected,
                unselectedIconColor = NavUnselected,
                unselectedTextColor = NavUnselected,
                indicatorColor      = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick  = {},
            icon     = {
                Icon(Icons.Default.Person, contentDescription = "Profile")
            },
            label    = { Text("PROFILE", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = NavSelected,
                selectedTextColor   = NavSelected,
                unselectedIconColor = NavUnselected,
                unselectedTextColor = NavUnselected,
                indicatorColor      = Color.Transparent
            )
        )
    }
}

// ── AnimatedWeatherBanner kept for backward-compat (unused in new flow) ────
@Composable
fun AnimatedWeatherBanner(type: EnvironmentType) {
    // Kept to avoid breaking any other call sites.
    // The new WeatherBanner(state) is used inside ShiftSafeDashboard above.
}
