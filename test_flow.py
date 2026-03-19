import sys
import os

root_dir = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.join(root_dir, "core-backend"))
sys.path.append(os.path.join(root_dir, "engine", "dynamic_pricing"))

from worker_profile import GigWorkerProfile
from pricing_engine import DynamicPricingEngine

# Terminal Color Codes for UI polish
class Colors:
    HEADER = '\033[95m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'

def run_integration_test():
    print(f"{Colors.CYAN}{Colors.BOLD}Initializing Winkit Enterprise Risk Platform...{Colors.ENDC}\n")
    try:
        engine = DynamicPricingEngine() 
    except Exception as e:
        print(f"{Colors.FAIL}Failed to initialize. Error: {e}{Colors.ENDC}")
        return

    test_cases = [
        {"id": "RIDER_N_001", "zone": "OMR_Tech_Park"}, 
        {"id": "RIDER_V_005", "zone": "Potheri_GST"}    
    ]

    for case in test_cases:
        rider_id = case["id"]
        operational_zone = case["zone"]
        
        print(f"{Colors.BLUE}{'='*75}{Colors.ENDC}")
        print(f"{Colors.BOLD} 📱 APP OPENED BY: {rider_id} | ZONE: {operational_zone} {Colors.ENDC}")
        print(f"{Colors.BLUE}{'='*75}{Colors.ENDC}")

        # Database Lookup
        print(f"\n{Colors.HEADER}[1] WORKER LEDGER (SQLite){Colors.ENDC}")
        profile = GigWorkerProfile(rider_id=rider_id)
        e_hour_info = profile.calculate_e_hour()
        
        if e_hour_info.get("status") == "error":
            print(f"    └── {Colors.FAIL}SYSTEM ACTION: Policy Rejected. Reason: {e_hour_info['message']}{Colors.ENDC}")
            continue
            
        v_loss = profile.get_v_loss_coverage(covered_hours=4)
        
        print(f"    ├── Rider Status: {e_hour_info.get('rider_type')}")
        print(f"    ├── Dynamic Wage: ₹{e_hour_info['e_hour']}/hr")
        print(f"    └── Exposure Limit: ₹{v_loss}/day")

        # Risk Assessment
        print(f"\n{Colors.HEADER}[2] AI RISK ASSESSMENT (Gemini 2.5 + OpenWeather){Colors.ENDC}")
        print("    ├── Fetching live telemetry...")
        quote = engine.calculate_weekly_premium(dynamic_v_loss=v_loss, zone_name=operational_zone)
        
        # Display Civic Reason
        reason = quote['dominant_civic_reason']
        short_reason = reason if len(reason) < 60 else reason[:57] + "..."
        print(f"    ├── Civic Agent: {short_reason}")
        
        # Weather Forecast
        print("    └── 7-Day Rain Forecast (PoP):")
        forecast = quote.get("weather_forecast_log", [0]*7)
        weather_str = ""
        for day, pop in enumerate(forecast):
            icon = "🌧️ " if pop > 40 else "☁️ " if pop > 10 else "☀️ "
            weather_str += f"        Day {day}: {pop:2}% {icon}\n"
        print(weather_str.rstrip())

        # Final Smart Quote
        print(f"\n{Colors.GREEN}{Colors.BOLD}[3] 🏆 FINAL SMART QUOTE 🏆{Colors.ENDC}")
        print(f"    ├── Daily Payout Limit:   ₹{quote['daily_payout_coverage_inr']:.2f}")
        print(f"    ├── Max Weekly Coverage:  ₹{quote['max_weekly_coverage_inr']:.2f}")
        print(f"    ├── Geospatial Penalty:   {Colors.WARNING}+{quote['applied_v_zone_penalty_percent']}%{Colors.ENDC} (Infrastructure Risk)")
        print(f"    └── Total Weekly Premium: {Colors.GREEN}{Colors.BOLD}₹{quote['final_weekly_premium_inr']:.2f}{Colors.ENDC}")
        print("\n")

if __name__ == "__main__":
    run_integration_test()
