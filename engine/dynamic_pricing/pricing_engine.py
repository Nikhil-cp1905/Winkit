import requests
import math
from datetime import datetime, date
import sys
import os

# The free tier weather forecast that is being utilized does give provide data for more than 7 days.
# So, we have Extrapolating the data of the first 5 days to determine the last 2


# Get current directory
current_dir = os.path.dirname(os.path.abspath(__file__))

# Get parent directory
parent_dir = os.path.dirname(current_dir)

# Get root directory 
root_dir = os.path.dirname(parent_dir)

# Add root to Python's path so it can find config.py
sys.path.append(root_dir)

try:
    from config import OPENWEATHER_API_KEY
except ImportError:
    print("Error: Could not find config.py in the root directory.")
    sys.exit(1)


# ==========================================
# CORE PRICING ENGINE
# ==========================================
class DynamicPricingEngine:
    def __init__(self, api_key: str):
        self.api_key = api_key
        self.base_url = "https://api.openweathermap.org/data/2.5"
        
        # Potheri, SRM Nagar coordinates
        self.lat = 12.8259
        self.lon = 80.0395
        
        # AI Tuning Constants
        self.k_time_decay = 0.05  
        self.w_variance = 0.40    

    def fetch_forecast_data(self) -> dict:
        # Pulls the 5-day / 3-hour forecast from OpenWeatherMap.
        endpoint = f"{self.base_url}/forecast?lat={self.lat}&lon={self.lon}&appid={self.api_key}&units=metric"
        response = requests.get(endpoint)
        response.raise_for_status()
        return response.json()

    def calculate_u_weather_array(self) -> list:
        # Calculates the daily U_weather risk penalty for a 7-day policy.
        raw_data = self.fetch_forecast_data()
        
        daily_pop = {}
        today = date.today()
        
        # Group 3-hour blocks by day to find the max probability of rain
        for block in raw_data.get('list', []):
            dt = datetime.fromtimestamp(block['dt']).date()
            pop = block.get('pop', 0.0) 
            if dt not in daily_pop or pop > daily_pop[dt]:
                daily_pop[dt] = pop

        policy_risk_array = []
        last_known_pop = 0.0
        
        # Loop strictly over 7 days to satisfy the Weekly pricing constraint
        for t in range(7):
            target_date = today.replace(day=today.day + t) 
            
            if target_date in daily_pop:
                p = daily_pop[target_date]
                last_known_pop = p
            else:
                p = last_known_pop  # Extrapolate for Day 6 & 7

            time_penalty = self.k_time_decay * math.sqrt(t)
            variance_penalty = self.w_variance * (p * (1.0 - p))
            u_weather = time_penalty + variance_penalty
            
            policy_risk_array.append({
                "day_index": t,
                "probability_p": round(p, 2),
                "time_penalty": round(time_penalty, 3),
                "variance_penalty": round(variance_penalty, 3),
                "u_weather_total": round(u_weather, 3)
            })

        return policy_risk_array

if __name__ == "__main__":
    engine = DynamicPricingEngine(api_key=OPENWEATHER_API_KEY)
    risk_array = engine.calculate_u_weather_array()
    
    print(f"{'Day':<5} | {'p (Rain %)':<12} | {'Time Decay':<12} | {'Variance':<12} | {'U_weather (Total Penalty)'}")
    print("-" * 70)
    
    for day in risk_array:
        print(f"t={day['day_index']:<2} | "
              f"{day['probability_p']:<12.2f} | "
              f"{day['time_penalty']:<12.3f} | "
              f"{day['variance_penalty']:<12.3f} | "
              f"{day['u_weather_total']:.3f} ({(day['u_weather_total']*100):.1f}%)")
