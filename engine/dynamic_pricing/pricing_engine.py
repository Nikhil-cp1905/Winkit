import math
from datetime import datetime, date, timedelta
import sys
import os
from config import OPENWEATHER_API_KEY, DEMO_MODE
current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(os.path.dirname(current_dir))
sys.path.append(root_dir)

from config import OPENWEATHER_API_KEY
from services.weather_api_client import WeatherAPIClient
from services.civic_risk_agent import CivicRiskAgent, fetch_live_chennai_headlines
from services.location_risk_service import LocationRiskService

class DynamicPricingEngine:
    def __init__(self):
        # Initializing Services
        self.weather_client = WeatherAPIClient(api_key=OPENWEATHER_API_KEY, demo_mode=DEMO_MODE)
        self.civic_agent = CivicRiskAgent()
        self.location_service = LocationRiskService()
        
        # AI Tuning Constants
        self.k_time_decay = 0.05  
        self.w_variance = 0.40    
        self.f_risk = 0.15        
        self.platform_fee = 10.00  

    def calculate_weekly_premium(self, dynamic_v_loss: float, zone_name: str = "Potheri_GST") -> dict:
        """Calculates the final Weekly Premium in INR using Multi-Peril Math and Empirical Localization."""
        
        # Fetching Weather Data
        raw_weather_data = self.weather_client.get_forecast()
        daily_pop = {}
        today = date.today()
        
        for block in raw_weather_data.get('list', []):
            dt = datetime.fromtimestamp(block['dt']).date()
            pop = block.get('pop', 0.0) 
            if dt not in daily_pop or pop > daily_pop[dt]:
                daily_pop[dt] = pop

        # Fetch Live Agentic Civic Risk
        print("   -> Fetching and analyzing live Chennai news...")
        live_news = fetch_live_chennai_headlines(max_headlines=5)
        # Pass the zone_name so the agent can apply the Epicenter Multiplier if needed
        civic_data = self.civic_agent.analyze_civic_risk(live_news, rider_zone=zone_name)
 
        base_p_civic = civic_data.get("p_civic", 0.0)
        civic_reason = civic_data.get("reason", "No disruption detected.")
        print(f"   -> Adjusted Civic Risk: {base_p_civic * 100}% ({civic_reason})")

        last_known_pop = 0.0
        total_weekly_premium = 0.0
 
        previous_day_water_risk = 0.0
        weather_forecast_log = []

        for t in range(7):
            target_date = today + timedelta(days=t)
 
            if target_date in daily_pop:
                raw_p_weather = daily_pop[target_date]
                last_known_pop = raw_p_weather
            else:
                # Decay the last known weather drastically because the forecast is over
                raw_p_weather = last_known_pop * 0.2  
                last_known_pop = raw_p_weather

            # Decay the civic risk over the week
            p_civic = base_p_civic * (0.5 ** t)

            # Hardcoding zone score for demonstration; in production, this would come from the LocationRiskService
            v_zone_score = 0.25  

            # Boost today's rain risk based on bad infrastructure
            infrastructure_multiplier = 1.0 + v_zone_score
            boosted_p_weather = min(raw_p_weather * infrastructure_multiplier, 1.0)

            # Calculate standing water spillover from yesterday
            # 16-hour dry time = ~0.66 retention, scaled by how bad the drains are
            spillover_retention = 0.66 * v_zone_score
            waterlogging_risk = previous_day_water_risk * spillover_retention

            # The final weather risk is the worse of the two
            effective_p_weather = max(boosted_p_weather, waterlogging_risk)
            
            # Pass today's effective risk to tomorrow's loop
            previous_day_water_risk = effective_p_weather 

            
            p_neither = (1.0 - effective_p_weather) * (1.0 - p_civic)
            p_union = 1.0 - p_neither

            # Expected Loss
            expected_loss_el = p_union * dynamic_v_loss

            # U_weather Risk Matrix (Using Union Probability)
            time_penalty = self.k_time_decay * math.sqrt(t)
            variance_penalty = self.w_variance * (p_union * (1.0 - p_union))
            u_risk = time_penalty + variance_penalty

            # The Beta Multiplier (V_zone is removed from here!)
            # We only add the base 1.0, the AI uncertainty, and the Fraud buffer
            raw_beta = 1.0 + u_risk + self.f_risk
            capped_beta = min(raw_beta, 2.5)

            # Final daily premium calculation
            daily_premium = capped_beta * expected_loss_el
            total_weekly_premium += daily_premium
    
            weather_forecast_log.append(round(raw_p_weather * 100))
        # Add the flat platform fee to the weekly total
        final_gross_premium = total_weekly_premium + self.platform_fee
        
        v_zone_score=0.002
        
        # Add the flat platform fee to the weekly total
        final_gross_premium = total_weekly_premium + self.platform_fee

        return {
            "final_weekly_premium_inr": round(final_gross_premium, 2),
            "daily_payout_coverage_inr": round(dynamic_v_loss, 2),
            "max_weekly_coverage_inr": round(dynamic_v_loss * 7, 2), 
            "dominant_civic_reason": civic_reason,
            "applied_v_zone_penalty_percent": round(v_zone_score * 100, 1),
            "weather_forecast_log": weather_forecast_log  # <-- ADD THIS LINE
        }
