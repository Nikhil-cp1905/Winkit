package com.example.winkit.ui.screens.dashboard

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.winkit.R // Ensure this matches your package name
import com.example.winkit.domain.models.DashboardState
import com.example.winkit.domain.models.EnvironmentType

@Composable
fun ShiftSafeDashboard(state: DashboardState, onTriggerAlert: () -> Unit) {
    // A clean, standard app background (light or dark mode friendly)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Soft, clean off-white/gray
    ) {
        // LAYER 1: The Top Animated Banner with Branding
        AnimatedWeatherBanner(state.environment)

        // LAYER 2: The Rest of the App UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // Sub-header
            Text(
                text = "Live Coverage Status",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Hyper-Local Metrics Card
            MetricsCard(state)
            
            Spacer(modifier = Modifier.weight(1f))
            
            // DEMO TRIGGER FOR HACKATHON
            Button(
                onClick = onTriggerAlert,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("DEMO: Trigger Blackout Alert", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AnimatedWeatherBanner(type: EnvironmentType) {
    val context = LocalContext.current
    
    // Setup Coil to decode GIFs properly based on Android version
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    // Determine which GIF to show (Make sure these exist in res/drawable!)
    // If you don't have them yet, comment out this 'when' block and just use a URL for testing.
    val gifResource = when (type) {
        EnvironmentType.HEATWAVE -> R.drawable.sun // Replace with your actual gif name
        EnvironmentType.RAIN -> R.drawable.rain
        EnvironmentType.SMOG -> R.drawable.smog
        EnvironmentType.NIGHT -> R.drawable.night
        EnvironmentType.CLEAR_DAY -> R.drawable.clearday
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp) // Banner height
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
    ) {
        // The GIF Background
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(gifResource) // Alternatively, put a URL string here like "https://media.giphy.com/..."
                .crossfade(true)
                .build(),
            imageLoader = imageLoader,
            contentDescription = "Weather Animation",
            contentScale = ContentScale.Crop, // Ensures the GIF fills the banner perfectly
            modifier = Modifier.fillMaxSize()
        )

        // The Dark Overlay so text is readable on top of the GIF
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        // Company Branding on top of the Banner
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(24.dp)
                .systemBarsPadding()
        ) {
            Text(
                text = "ShiftSafe",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = "Active Protection Mode",
                color = Color(0xFF00FF87), // Neon green accent
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MetricsCard(state: DashboardState) {
    // A much cleaner, standard white card for the metrics
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            MetricRow("Wet-Bulb Temp", "${state.wetBulbTemp}°C", if (state.wetBulbTemp > 30) Color(0xFFD32F2F) else Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))
            MetricRow("Air Quality (AQI)", "${state.aqi}", if (state.aqi > 300) Color(0xFFD32F2F) else Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))
            MetricRow("Store Status", state.storeStatus, Color(0xFF2E7D32)) // Clean green
        }
    }
}

@Composable
fun MetricRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(text = value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Black)
    }
}
