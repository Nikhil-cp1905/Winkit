# test_flow.py
import sys
import os

root_dir = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.join(root_dir, "core-backend"))
sys.path.append(os.path.join(root_dir, "engine", "dynamic_pricing"))

from worker_profile import GigWorkerProfile
from pricing_engine import DynamicPricingEngine

def run_integration_test():
    print("Initializing AI Risk Engine and Shared Services...")
    try:
        engine = DynamicPricingEngine() 
    except Exception as e:
        print(f"Failed to initialize. Error: {e}")
        return

    test_riders = [
        "RIDER_N_001", # New Rider
        "RIDER_V_005", # Veteran
        "RIDER_V_003"  # veteran 
    ]

    for rider_id in test_riders:
        print("\n" + "="*65)
        print(f" 📱 APP OPENED BY: {rider_id}")
        print("="*65)

        print("-> Fetching worker history from database...")
        profile = GigWorkerProfile(rider_id=rider_id)
        e_hour_info = profile.calculate_e_hour()
        
        print(f"-> Rider Status: {e_hour_info.get('rider_type', 'Status Error')}")
        
        if e_hour_info.get("status") == "error":
            print(f"-> SYSTEM ACTION: Policy Rejected. Reason: {e_hour_info['message']}")
            continue
            
        print(f"-> Calculated E_hour: ₹{e_hour_info['e_hour']}/hr")

        v_loss = profile.get_v_loss_coverage(covered_hours=4)
        print(f"-> Policy Coverage Limit (V_loss): ₹{v_loss}")

        print("-> Pinging OpenWeather API & Calculating AI Risk Premium...")
        quote = engine.calculate_weekly_premium(dynamic_v_loss=v_loss)
        
        print(f"\n✅ FINAL QUOTE FOR {rider_id}")
        print(f"   Guaranteed Payout: ₹{quote['payout_coverage_inr']:.2f}")
        print(f"   Weekly Premium:    ₹{quote['final_weekly_premium_inr']:.2f}")

if __name__ == "__main__":
    run_integration_test()
