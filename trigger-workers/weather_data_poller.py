import requests
import json
from datetime import datetime

class PotheriWeatherIngestor:
    def __init__(self, api_key: str):
        self.api_key = api_key
        self.base_url = "https://api.openweathermap.org/data/2.5"
        
        # Exact coordinates for Potheri, SRM Nagar, Chennai
        self.lat = 12.8259
        self.lon = 80.0395

    def pull_realtime_data(self) -> dict:
        """
        Pulls the current live weather data for Potheri.
        Useful for testing Phase 2 automated triggers.
        """
        endpoint = f"{self.base_url}/weather?lat={self.lat}&lon={self.lon}&appid={self.api_key}&units=metric"
        
        try:
            response = requests.get(endpoint)
            response.raise_for_status()
            data = response.json()
            
            # Extracting just the useful bits for your evaluation
            extracted_data = {
                "timestamp": datetime.now().isoformat(),
                "location": data.get("name", "Unknown Location"),
                "main_condition": data.get("weather", [{}])[0].get("main"),
                "description": data.get("weather", [{}])[0].get("description"),
                "temperature_celsius": data.get("main", {}).get("temp"),
                "rain_last_1h_mm": data.get("rain", {}).get("1h", 0.0) # This is your main trigger metric
            }
            return extracted_data
            
        except requests.exceptions.RequestException as e:
            return {"error": f"Failed to fetch realtime data: {e}"}

    def pull_forecast_data(self) -> dict:
        """
        Pulls the forecast data (in 3-hour blocks) for Potheri.
        Useful for evaluating the Phase 1 Weekly Premium probability.
        """
        endpoint = f"{self.base_url}/forecast?lat={self.lat}&lon={self.lon}&appid={self.api_key}&units=metric"
        
        try:
            response = requests.get(endpoint)
            response.raise_for_status()
            data = response.json()
            
            # Grabbing the first 5 blocks (next 15 hours) just for evaluation readability
            forecast_preview = []
            for block in data.get("list", [])[:5]:
                forecast_preview.append({
                    "time": block.get("dt_txt"),
                    "predicted_condition": block.get("weather", [{}])[0].get("main"),
                    "predicted_rain_3h_mm": block.get("rain", {}).get("3h", 0.0)
                })
                
            return {
                "total_blocks_available": len(data.get("list", [])),
                "forecast_preview": forecast_preview
            }
            
        except requests.exceptions.RequestException as e:
            return {"error": f"Failed to fetch forecast data: {e}"}

# --- Evaluation Execution ---
if __name__ == "__main__":
    import sys
    import os
    current_dir = os.path.dirname(os.path.abspath(__file__))
    root_dir = os.path.dirname(current_dir)
    sys.path.append(root_dir)
    
    try:
        from config import OPENWEATHER_API_KEY
    except ImportError:
        print("Error: Could not find config.py. Make sure it is in the root directory.")
        sys.exit(1)

    ingestor = PotheriWeatherIngestor(api_key=OPENWEATHER_API_KEY)
    
    print("--- LIVE WEATHER IN POTHERI ---")
    live_data = ingestor.pull_realtime_data()
    print(json.dumps(live_data, indent=4))
    
    print("\n--- FORECAST DATA FOR POTHERI ---")
    forecast_data = ingestor.pull_forecast_data()
    print(json.dumps(forecast_data, indent=4))
