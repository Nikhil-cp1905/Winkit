import os
import sys

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

from services.h3_risk_engine import H3RiskEngine
from services.civic_risk_agent import CivicRiskAgent, fetch_live_chennai_headlines

class EventEvaluator:
    def __init__(self):
        print("⚙️  Initializing Winkit Parametric Orchestrator...")
        # 1. Instantiate the Two Brains
        self.physics_engine = H3RiskEngine()
        self.civic_engine = CivicRiskAgent()
        
        # 2. The Smart Contract Execution Threshold
        # If the combined probability of disruption crosses 65%, we pay the rider.
        self.PAYOUT_THRESHOLD = 0.65 

    def evaluate_worker_zone(self, worker_id: str, lat: float, lng: float, zone_name: str, raw_weather_api: float, live_news: list):
        """
        The main evaluation loop. Processes physics and civic risks to determine payouts.
        """
        print(f"\n" + "="*50)
        print(f"🔍 EVALUATING RIDER: {worker_id} | ZONE: {zone_name.upper()}")
        print("="*50)

        # --- BRAIN 1: THE PHYSICS ENGINE ---
        print("🌧️  Running H3 Physics Engine...")
        physics_result = self.physics_engine.calculate_effective_risk(lat, lng, raw_weather_api)
        p_effective = physics_result["p_effective_final"]
        
        print(f"   ├─ H3 Hexagon: {physics_result['hex_id']}")
        print(f"   ├─ Raw Weather: {physics_result['raw_weather_api']}")
        print(f"   ├─ V_zone (Drainage): +{physics_result['v_zone_applied']}")
        print(f"   └─ Output p_effective: {p_effective}")

        # --- BRAIN 2: THE CIVIC ENGINE ---
        print("\n📰 Running Agentic Civic Engine...")
        civic_result = self.civic_engine.analyze_civic_risk(live_news, rider_zone=zone_name)
        p_civic = civic_result["p_civic"]
        
        print(f"   ├─ Event Classification: {civic_result['classification']}")
        print(f"   ├─ LLM Reasoning: {civic_result['reason']}")
        print(f"   └─ Output p_civic: {p_civic}")

        # --- THE UNION PROBABILITY MATH ---
        # Probability of (A OR B) = 1 - ( (1-A) * (1-B) )
        print("\n🧮 Calculating Union Probability...")
        combined_risk = 1.0 - ((1.0 - p_effective) * (1.0 - p_civic))
        combined_risk = round(combined_risk, 3)
        
        print(f"   └─ TOTAL DISRUPTION RISK: {combined_risk * 100}%")

        # --- THE SMART CONTRACT TRIGGER ---
        print("\n⚖️  Smart Contract Decision:")
        if combined_risk >= self.PAYOUT_THRESHOLD:
            print(f"   🚨 BREACH DETECTED (> {self.PAYOUT_THRESHOLD * 100}%). TRIGGERING ZERO-TOUCH PAYOUT!")
            return self._trigger_payout(worker_id, zone_name, combined_risk)
        else:
            print(f"   ✅ Conditions acceptable. Rider is safe. Monitoring continues.")
            return False

    def _trigger_payout(self, worker_id: str, zone_name: str, risk_score: float):
        """
        Queues the payout for the Razorpay UPI integration.
        """
        # In a full production environment, this pushes a payload to a Redis/Kafka queue.
        print(f"   💸 [ESCROW] Queued payout for {worker_id} in {zone_name}. Awaiting 7-Layer Fraud Check.")
        return True


# --- SYSTEM INTEGRATION TEST ---
if __name__ == "__main__":
    evaluator = EventEvaluator()
    
    # Rider Profile
    w_id = "WKT-8842"
    w_lat, w_lng = 12.8236, 80.0435 # Coordinates for Potheri
    w_zone = "Potheri"
    
    # We fetch real news once to use across our test scenarios
    print("\n📡 Fetching Live News for Testing...")
    actual_live_news = fetch_live_chennai_headlines(max_headlines=3)
    
    # --- SCENARIO 1: The "Invisible" Flood ---
    # API says only 20% rain, but the H3 engine remembers yesterday's storm and bad drainage.
    print("\n\n>>> TEST SCENARIO 1: The Invisible Flood (Physics Heavy)")
    evaluator.evaluate_worker_zone(
        worker_id=w_id, lat=w_lat, lng=w_lng, zone_name=w_zone,
        raw_weather_api=0.20, # Only 20% chance of rain today
        live_news=["Normal day in Chennai", "Traffic moving smoothly"] # No civic risk
    )
    
    # --- SCENARIO 2: The Clear Sky Riot ---
    # 0% rain, but a massive political rally completely shuts down GST road.
    print("\n\n>>> TEST SCENARIO 2: The Clear Sky Riot (Civic Heavy)")
    evaluator.evaluate_worker_zone(
        worker_id=w_id, lat=w_lat, lng=w_lng, zone_name=w_zone,
        raw_weather_api=0.00, # Perfect weather
        live_news=["Massive riots blocking GST road in Potheri", "Section 144 imposed", "Total shutdown"]
    )
    
    # --- SCENARIO 3: The Multiplier Effect (Union Probability) ---
    # Minor rain (30%) + Minor friction (30%). Neither triggers a payout alone.
    # But together? The math pushes them over the edge.
    print("\n\n>>> TEST SCENARIO 3: The Multiplier Effect (Union Math)")
    evaluator.evaluate_worker_zone(
        worker_id=w_id, lat=w_lat, lng=w_lng, zone_name=w_zone,
        raw_weather_api=0.30, # Minor rain
        live_news=["VIP movement causing localized friction in Potheri", "Traffic slow"]
    )
