import h3
from typing import Dict, Any

class H3RiskEngine:
    def __init__(self):
        # Resolution 9 is ~0.1 sq km hexagons
        self.RESOLUTION = 9
     
        # we still need to integrate the database 
        self.zone_state_db = {
            # Example 1: Terrible drainage (High V_zone), flooded yesterday
            '8961892a03bffff': {'v_zone_score': 0.45, 'yesterday_water': 0.80},
            
            # Example 2: Excellent infrastructure, dry yesterday
            '8961892a033ffff': {'v_zone_score': 0.05, 'yesterday_water': 0.00}
        }

    def get_h3_index(self, lat: float, lng: float) -> str:
        """Converts raw GPS coordinates into the Uber H3 Hexagon ID."""
        return h3.latlng_to_cell(lat, lng, self.RESOLUTION)

    def fetch_zone_state(self, hex_id: str) -> Dict[str, float]:
        """
        Retrieves the empirical infrastructure state for the hex.
        Defaults to a standard 0.20 penalty if the zone is new to our DB.
        """
        return self.zone_state_db.get(hex_id, {'v_zone_score': 0.20, 'yesterday_water': 0.00})

    def calculate_effective_risk(self, lat: float, lng: float, raw_p_weather: float) -> Dict[str, Any]:
        """
        Executes the Stateful Spillover Math to determine true physical risk.
        """
        hex_id = self.get_h3_index(lat, lng)
        state = self.fetch_zone_state(hex_id)
        
        v_zone = state['v_zone_score']
        yesterday_water = state['yesterday_water']

        # 1. Infrastructure Water Boosting (p_boosted)
        # Equation: p_boosted = min(p_weather * (1.0 + V_zone), 1.0)
        p_boosted = min(raw_p_weather * (1.0 + v_zone), 1.0)

        # 2. Stateful Spillover Retention (p_spillover)
        # Equation: p_spillover = p_yesterday * (0.66 * V_zone)
        p_spillover = yesterday_water * (0.66 * v_zone)

        # 3. Effective Weather Risk
        p_effective = max(p_boosted, p_spillover)

        return {
            "hex_id": hex_id,
            "v_zone_applied": v_zone,
            "raw_weather_api": round(raw_p_weather, 3),
            "p_boosted": round(p_boosted, 3),
            "p_spillover": round(p_spillover, 3),
            "p_effective_final": round(p_effective, 3)
        }

    def update_eod_state(self, hex_id: str, today_effective_water: float):
        """
        Called at midnight by the Cron Job to roll today's water into tomorrow's state.
        """
        if hex_id in self.zone_state_db:
            self.zone_state_db[hex_id]['yesterday_water'] = today_effective_water
        else:
            self.zone_state_db[hex_id] = {'v_zone_score': 0.20, 'yesterday_water': today_effective_water}


# --- LOCAL TESTING ---
if __name__ == "__main__":
    engine = H3RiskEngine()
    
    # Simulating a rider in Chennai (near Velachery/Pallikaranai area)
    # This coordinate maps to our mocked '8961892a03bffff' which has terrible drainage.
    test_lat, test_lng = 12.9815, 80.2230 
    
    # Scenario: The OpenWeather API says it's a perfectly sunny day (0% rain)
    api_forecast = 0.00 
    
    print("\n--- WINKIT H3 HYPER-LOCALIZATION ENGINE ---")
    print(f"📡 API Forecast: {api_forecast * 100}% chance of rain")
    
    result = engine.calculate_effective_risk(test_lat, test_lng, api_forecast)
    
    print(f"📍 H3 Hexagon:   {result['hex_id']}")
    print(f"🏗️  V_zone Score: +{result['v_zone_applied'] * 100}% (Drainage Penalty)")
    print(f"🌊 Spillover:    {result['p_spillover'] * 100}% (Standing water from yesterday)")
    print(f"🚨 TRUE RISK:    {result['p_effective_final'] * 100}%")
    print("-" * 43)
