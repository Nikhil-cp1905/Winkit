import sys
import os
import time

# Adjust path to import from root directory
root_dir = os.path.dirname(os.path.abspath(__file__))
sys.path.append(root_dir)

from config import DEMO_MODE
from services.event_evaluator import EventEvaluator
from services.civic_risk_agent import fetch_live_chennai_headlines

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
    print(f"{Colors.CYAN}{Colors.BOLD}Initializing Winkit Enterprise Risk Platform...{Colors.ENDC}")
    if DEMO_MODE:
        print(f"{Colors.WARNING}⚠️  DEMO_MODE is ON. Traffic telemetry will be mocked.{Colors.ENDC}\n")
    else:
        print(f"{Colors.GREEN}🌍 LIVE MODE is ON. Fetching real TomTom traffic data.{Colors.ENDC}\n")
        
    time.sleep(1)
    
    # 1. Initialize the Orchestrator
    try:
        evaluator = EventEvaluator()
    except Exception as e:
        print(f"{Colors.FAIL}Failed to initialize Event Evaluator. Error: {e}{Colors.ENDC}")
        return
        
    # 2. Fetch Live News for the Simulation
    print(f"{Colors.HEADER}[1] FETCHING LIVE TELEMETRY & RSS FEEDS (CHENNAI){Colors.ENDC}")
    time.sleep(1)
    live_news = fetch_live_chennai_headlines(max_headlines=3)
    for i, news in enumerate(live_news):
        print(f"   {Colors.BLUE}>> {news}{Colors.ENDC}")

    print("\n" + "="*75)
    print(f"{Colors.BOLD} 🚨 COMMENCING LIVE PARAMETRIC EVALUATION (15-MIN CRON CYCLE) 🚨{Colors.ENDC}")
    print("="*75)

    # --- SCENARIO 1: The "Invisible" Flood ---
    print(f"\n{Colors.HEADER}{Colors.BOLD}>>> SCENARIO 1: The 'Invisible' Flood (H3 Physics Engine){Colors.ENDC}")
    print(f"{Colors.CYAN}Context: Sunny day (0% rain). But Rider is in a low-lying H3 Hexagon with standing water.{Colors.ENDC}")
    time.sleep(2)
    
    evaluator.evaluate_worker_zone(
        worker_id="WKT-1001",
        lat=12.9815, lng=80.2230, # Low-lying Velachery coords
        zone_name="Velachery",
        raw_weather_api=0.00, 
        live_news=["Traffic is smooth", "Clear skies"]
    )
    
    time.sleep(3)

    # --- SCENARIO 2: The Agentic Override ---
    print(f"\n{Colors.HEADER}{Colors.BOLD}>>> SCENARIO 2: The Agentic Override (Hardware vs Software){Colors.ENDC}")
    print(f"{Colors.CYAN}Context: Fake news claims a 'Total Shutdown'. TomTom Traffic API checks the reality.{Colors.ENDC}")
    time.sleep(2)
    
    evaluator.evaluate_worker_zone(
        worker_id="WKT-2044",
        lat=12.8236, lng=80.0435, # Potheri coords
        zone_name="Potheri",
        raw_weather_api=0.00,
        live_news=["Section 144 imposed in Potheri", "Total Shutdown and riots!"]
    )
    
    time.sleep(3)

    # --- SCENARIO 3: The Multiplier Effect ---
    print(f"\n{Colors.HEADER}{Colors.BOLD}>>> SCENARIO 3: The Multiplier Effect (Union Probability){Colors.ENDC}")
    print(f"{Colors.CYAN}Context: Moderate Rain (30%) + Localized VIP Friction (30%). Do they compound?{Colors.ENDC}")
    time.sleep(2)
    
    evaluator.evaluate_worker_zone(
        worker_id="WKT-3099",
        lat=12.8988, lng=80.2268, # OMR Tech Park coords
        zone_name="OMR_Tech_Park",
        raw_weather_api=0.30,
        live_news=["VIP movement causing localized friction near OMR", "Traffic is slow"]
    )

    print(f"\n{Colors.GREEN}{Colors.BOLD}✅ ALL SMART CONTRACTS EVALUATED SUCCESSFULLY.{Colors.ENDC}\n")


if __name__ == "__main__":
    try:
        run_integration_test()
    except KeyboardInterrupt:
        print(f"\n\n{Colors.FAIL}🛑 Simulation terminated by user.{Colors.ENDC}")
        sys.exit(0)
