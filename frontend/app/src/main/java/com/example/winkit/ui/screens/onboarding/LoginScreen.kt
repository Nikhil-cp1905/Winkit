package com.example.winkit.ui.screens.onboarding

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock // Safe default icon!
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.winkit.data.FirebaseAuthHelper

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    // UI States
    var isOtpSent by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Grab the Activity context for Firebase
    val context = LocalContext.current
    val activity = context as? Activity
    
    // Initialize our Firebase Helper
    val authHelper = remember(activity) { activity?.let { FirebaseAuthHelper(it) } }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
        // LAYER 1: The Top Gradient Background
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0A2A59), Color(0xFF006C7A)) // Dark Blue to Teal
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f) // Takes up top 65% of screen
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(gradientBrush)
                .padding(32.dp)
                .systemBarsPadding()
        ) {
            Column {
                // Shield/Lock Icon
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security",
                        tint = Color(0xFFFFC107), // Gold accent
                        modifier = Modifier.padding(12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Titles
                Text(
                    text = "WinkIT\nInstant Payout in a WINK",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 40.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Enter your registered number to get started with your active delivery coverage",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // LAYER 2: The Interactive Card at the bottom
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 16.dp), // Extra padding for the bottom of the screen
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            // Smoothly animate between Phone UI and OTP UI
            AnimatedContent(
                targetState = isOtpSent,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "login_transition"
            ) { targetState ->
                if (!targetState) {
                    PhoneInputView(
                        phoneNumber = phoneNumber,
                        onPhoneChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) phoneNumber = it },
                        isLoading = isLoading,
                        onSendOtp = {
                            if (phoneNumber.length == 10 && authHelper != null) {
                                isLoading = true
                                authHelper.sendOtp(
                                    phoneNumber = phoneNumber,
                                    onCodeSent = {
                                        isLoading = false
                                        isOtpSent = true
                                    },
                                    onError = { errorMsg ->
                                        isLoading = false
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        }
                    )
                } else {
                    OtpInputView(
                        phoneNumber = phoneNumber,
                        otpCode = otpCode,
                        onOtpChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) otpCode = it },
                        isLoading = isLoading,
                        onEditPhone = { isOtpSent = false; otpCode = "" }, 
                        onVerify = {
                            if (otpCode.length == 6 && authHelper != null) {
                                isLoading = true
                                authHelper.verifyOtp(
                                    code = otpCode,
                                    onSuccess = {
                                        isLoading = false
                                        onLoginSuccess() 
                                    },
                                    onError = { errorMsg ->
                                        isLoading = false
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneInputView(phoneNumber: String, onPhoneChange: (String) -> Unit, isLoading: Boolean, onSendOtp: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("PHONE NUMBER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Country Code Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.height(56.dp).wrapContentWidth()
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🇮🇳 +91", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Phone Input
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                placeholder = { Text("98765 43210", color = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF0A2A59)
                )
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onSendOtp,
            enabled = phoneNumber.length == 10 && !isLoading, // Disable if not 10 digits or if loading
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A2A59),
                disabledContainerColor = Color.LightGray
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Send OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("New here? Sign Up", color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Terms of Service  •  Privacy Policy", color = Color.LightGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun OtpInputView(phoneNumber: String, otpCode: String, onOtpChange: (String) -> Unit, isLoading: Boolean, onEditPhone: () -> Unit, onVerify: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("ENTER OTP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = otpCode,
            onValueChange = onOtpChange,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            placeholder = { Text("Enter 6-digit OTP", color = Color.LightGray) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF0A2A59)
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 18.sp, letterSpacing = 8.sp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Sent to +91 $phoneNumber ", color = Color.Gray, fontSize = 12.sp)
            Text(
                text = "Edit", 
                color = Color(0xFF0A2A59), 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onEditPhone() }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onVerify,
            enabled = otpCode.length == 6 && !isLoading, // Firebase requires 6 digits
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A2A59),
                disabledContainerColor = Color.LightGray
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Verify & Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}
