package com.example.winkit.ui.screens.dashboard

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.winkit.data.WeatherNetwork
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    // UI State Variables
    var temperature by mutableStateOf("...")
        private set
    var weatherCondition by mutableStateOf("Loading...")
        private set
    var humidity by mutableStateOf("...")
        private set
    var rainProbability by mutableStateOf("...")
        private set
    var cityName by mutableStateOf("Fetching...")
        private set

    init {
        fetchLiveWeather()
    }

    private fun fetchLiveWeather() {
        viewModelScope.launch {
            try {
                val response = WeatherNetwork.api.getCurrentWeather(city = "Chennai")
                
                // 1. PRINT SUCCESS TO LOGCAT!
                Log.d("WinkitAPI", "✅ SUCCESS! Live Temp is: ${response.main.temp}")
                
                temperature = response.main.temp.toInt().toString()
                weatherCondition = response.weather.firstOrNull()?.main ?: "Clear"
                humidity = response.main.humidity.toString()
                cityName = response.name

                rainProbability = if (weatherCondition.contains("Rain", ignoreCase = true)) {
                    "85%"
                } else if (weatherCondition.contains("Cloud", ignoreCase = true)) {
                    "30%"
                } else {
                    "5%"
                }

            } catch (e: Exception) {
                // 2. PRINT THE EXACT ERROR SO YOU CAN FIX IT
                Log.e("WinkitAPI", "❌ API FAILED: ${e.message}")
                
                // 3. CHANGE FALLBACKS TO "ERR" SO YOU KNOW IT BROKE
                temperature = "ERR"
                weatherCondition = "Offline"
                humidity = "ERR"
                rainProbability = "ERR"
                cityName = "Disconnected"
            }
        }
    }}
