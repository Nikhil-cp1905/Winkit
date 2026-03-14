import math
from datetime import datetime, date, timedelta
import sys
import os

# --- Pathing to root ---
current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(os.path.dirname(current_dir))
sys.path.append(root_dir)

from config import OPENWEATHER_API_KEY
from services.weather_api_client import WeatherAPIClient
# NEW: Import your Agentic AI and the live news fetcher
from services.civic_risk_agent import CivicRiskAgent, fetch_live_chennai_headlines

class DynamicPricingEngine:
    def __init__(self):
        # Initialize Both Shared Services
        self.weather_client = WeatherAPIClient(api_key=OPENWEATHER_API_KEY, demo_mode=False)
        self.civic_agent = CivicRiskAgent()
        
        # AI Tuning Constants
        self.k_time_decay = 0.05  
        self.w_variance = 0.40    
        self.v_zone = 1.10        
        self.f_risk = 0.15        
        self.base_cost_c = 10.00  

    def calculate_weekly_premium(self, dynamic_v_loss: float) -> dict:
        """Calculates the final Weekly Premium in INR using Multi-Peril Math."""
        
        # 1. Ask the shared service for the weather data
        raw_weather_data = self.weather_client.get_forecast()
        daily_pop = {}
        today = date.today()
        
        for block in raw_weather_data.get('list', []):
            dt = datetime.fromtimestamp(block['dt']).date()
            pop = block.get('pop', 0.0) 
            if dt not in daily_pop or pop > daily_pop[dt]:
                daily_pop[dt] = pop

        # 2. Ask the Agentic AI for Live Civic Risk
        print("   -> Fetching and analyzing live Chennai news...")
        live_news = fetch_live_chennai_headlines(max_headlines=5)
        civic_data = self.civic_agent.analyze_civic_risk(live_news)
        
        base_p_civic = civic_data.get("p_civic", 0.0)
        civic_reason = civic_data.get("reason", "No disruption detected.")
        print(f"   -> Agentic Civic Risk: {base_p_civic * 100}% ({civic_reason})")

        last_known_pop = 0.0
        total_weekly_premium = 0.0
        
        # 3. The Multi-Peril Math Loop
        for t in range(7):
            target_date = today + timedelta(days=t)
            
            p_weather = daily_pop.get(target_date, last_known_pop)
            if target_date in daily_pop:
                last_known_pop = p_weather

            # Decay the civic risk: A riot today is high risk, but likely clears up in a few days
            # We use an exponential decay (e.g., halves every day)
            p_civic = base_p_civic * (0.5 ** t)

            # THE CORE MULTI-PERIL LOGIC: Take the maximum of the two risks
            p_max = max(p_weather, p_civic)

            # 1. Calculate P_disruption based on the local zone
            p_disruption = min((p_max * self.v_zone), 1.0)

            # 2. Pure Expected Loss
            expected_loss_el = p_disruption * dynamic_v_loss

            # 3. Calculate U_weather (Theta Decay + Variance) using the dominant risk
            time_penalty = self.k_time_decay * math.sqrt(t)
            variance_penalty = self.w_variance * (p_max * (1.0 - p_max))
            u_risk = time_penalty + variance_penalty

            # 4. Final Risk Multiplier (Beta)
            beta = 1.0 + u_risk + self.f_risk

            # 5. Final daily premium
            daily_premium = expected_loss_el * beta
            total_weekly_premium += daily_premium
        
        final_gross_premium = total_weekly_premium + self.base_cost_c

        return {
            "final_weekly_premium_inr": round(final_gross_premium, 2),
            "payout_coverage_inr": round(dynamic_v_loss, 2),
            "dominant_civic_reason": civic_reason
        }

if __name__ == "__main__":
    engine = DynamicPricingEngine()
    quote = engine.calculate_weekly_premium(dynamic_v_loss=480.00)
    print(f"\nStandalone Test Quote: ₹{quote['final_weekly_premium_inr']}")
