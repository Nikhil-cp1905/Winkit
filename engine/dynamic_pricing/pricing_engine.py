import math
from datetime import datetime, date, timedelta
import sys
import os

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(os.path.dirname(current_dir))
sys.path.append(root_dir)

from config import OPENWEATHER_API_KEY
from services.weather_api_client import WeatherAPIClient

class DynamicPricingEngine:
    def __init__(self):
        # We instantiate the shared service instead of hardcoding API logic here
        # We have included demo_mode which will return fixed data for testing purposes, you can set it to False to get real data from OpenWeather API
        self.weather_client = WeatherAPIClient(api_key=OPENWEATHER_API_KEY, demo_mode=True)
        
        # AI Tuning Constants
        self.k_time_decay = 0.05
        self.w_variance = 0.40
        self.v_zone = 1.10
        self.f_risk = 0.15
        self.base_cost_c = 10.00

    def calculate_weekly_premium(self, dynamic_v_loss: float) -> dict:
        """
        Calculates the final Weekly Premium in INR.

        Args:
            dynamic_v_loss (float): Absolute expected loss 
        
        Returns: Final weekly premium in INR
                 payout coverage in INR
        """

        # Ask the shared service for the data
        raw_data = self.weather_client.get_forecast()
        
        daily_pop = {}
        today = date.today()
        
        # The data provided by openweather is only uptil 5 days, so we will predict the data for the last 6 and 7 day
        for block in raw_data.get('list', []):
            dt = datetime.fromtimestamp(block['dt']).date()
            pop = block.get('pop', 0.0) 
            if dt not in daily_pop or pop > daily_pop[dt]:
                daily_pop[dt] = pop

        last_known_pop = 0.0
        total_weekly_premium = 0.0

        for t in range(7):
            target_date = today + timedelta(days=t)
            
            p = daily_pop.get(target_date, last_known_pop)
            if target_date in daily_pop:
                last_known_pop = p

            #  Calculate P_disruption based on the local zone
            p_disruption = min((p * self.v_zone), 1.0)

            #  Pure Expected Loss (Using the individualized worker data)
            expected_loss_el = p_disruption * dynamic_v_loss

            #  Calculate U_weather (Theta Decay + Variance)
            time_penalty = self.k_time_decay * math.sqrt(t)
            variance_penalty = self.w_variance * (p * (1.0 - p))
            u_weather = time_penalty + variance_penalty

            #  Final Risk Multiplier (Beta)
            beta = 1.0 + u_weather + self.f_risk

            #  Final daily premium added to the accumulator
            daily_premium = expected_loss_el * beta
            total_weekly_premium += daily_premium
        
        final_gross_premium = total_weekly_premium + self.base_cost_c

        return {
            "final_weekly_premium_inr": round(final_gross_premium, 2),
            "payout_coverage_inr": round(dynamic_v_loss, 2)
        }
