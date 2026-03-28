package com.example.winkit.ui.screens.alerts

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RelocationAlertModal(onAccept: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF900000) // Deep alert red
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Alert Header
            Column {
                Text(
                    text = "⚠️ DARK STORE BLACKOUT",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sector 4 Store is offline. We are relocating you to Sector 18 to prevent income loss.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp
                )
            }

            // Map Placeholder (For Pitch Video)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 24.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🗺️ A* Route Map Placeholder", color = Color.White)
            }

            // The Satisfying Swipe Slider
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SwipeToAcceptSlider(
                    payout = "₹150 for 35 mins",
                    onSwipeComplete = onAccept
                )
            }
        }
    }
}

@Composable
fun SwipeToAcceptSlider(payout: String, onSwipeComplete: () -> Unit) {
    val sliderWidth = 320.dp
    val thumbSize = 64.dp
    val sliderWidthPx = with(LocalDensity.current) { sliderWidth.toPx() }
    val thumbSizePx = with(LocalDensity.current) { thumbSize.toPx() }
    val maxDragPx = sliderWidthPx - thumbSizePx

    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .width(sliderWidth)
            .height(thumbSize)
            .background(Color.Black, RoundedCornerShape(32.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        // Background Text
        Text(
            text = "Swipe to Accept $payout >>>",
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.Center),
            fontSize = 14.sp
        )

        // Draggable Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .size(thumbSize)
                .background(Color(0xFF00FF87), RoundedCornerShape(32.dp))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (offsetX.value > maxDragPx * 0.7f) {
                                    // Swipe successful
                                    offsetX.animateTo(maxDragPx)
                                    onSwipeComplete()
                                } else {
                                    // Snap back
                                    offsetX.animateTo(0f)
                                }
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            val newOffset = (offsetX.value + dragAmount).coerceIn(0f, maxDragPx)
                            offsetX.snapTo(newOffset)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text("→", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
