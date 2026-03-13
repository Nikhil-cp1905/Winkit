import os
import sys

# Path setup to import from database.py
current_dir = os.path.dirname(os.path.abspath(__file__))
if current_dir not in sys.path:
    sys.path.append(current_dir)

from database import SessionLocal, GigWorker

class GigWorkerProfile:
    def __init__(self, rider_id: str, zone_baseline_e_hour: float = 100.00):
        self.rider_id = rider_id
        self.zone_baseline_e_hour = zone_baseline_e_hour
        self.data_threshold_hours = 20.0  

    def fetch_ledger_data(self) -> dict:
        """Queries the SQLite database for the worker's historical data."""
        db = SessionLocal()
        try:
            # Standard SQLAlchemy Query
            worker = db.query(GigWorker).filter(GigWorker.rider_id == self.rider_id).first()
            
            if worker:
                return {
                    "account_status": worker.account_status,
                    "last_4_weeks_earnings_inr": worker.last_4_weeks_earnings,
                    "last_4_weeks_hours_active": worker.last_4_weeks_hours
                }
            return None
        finally:
            db.close()

    def calculate_e_hour(self) -> dict:
        """Calculates the dynamic Expected Hourly Wage."""
        worker_data = self.fetch_ledger_data()
        
        if not worker_data:
            return {"status": "error", "message": "Rider not found in system."}
            
        # SECURITY CHECK: Reject suspended accounts
        if worker_data["account_status"] == "Suspended":
            return {"status": "error", "message": "Account suspended. Ineligible for policy."}

        hours_active = worker_data["last_4_weeks_hours_active"]
        earnings = worker_data["last_4_weeks_earnings_inr"]

        # Cold Start: Brand New Rider
        if hours_active == 0:
            return {"rider_type": "New (Zero Data)", "e_hour": self.zone_baseline_e_hour}
            
        # Transition Phase
        elif hours_active < self.data_threshold_hours:
            return {"rider_type": "New (Transitioning)", "e_hour": self.zone_baseline_e_hour}
            
        # Veteran Phase: Fully personalized moving average
        else:
            actual_e_hour = round(earnings / hours_active, 2)
            
            # FRAUD DETECTION CHECK (Edge Case G from our CSV)
            if actual_e_hour > 1000:
                return {"status": "error", "message": "Fraud Alert: Earnings/Hour ratio exceeds physical limits."}
                
            return {"rider_type": "Veteran", "e_hour": actual_e_hour}

    def get_v_loss_coverage(self, covered_hours: int = 4) -> float:
        """Returns the final V_loss (Payout Amount) for the pricing engine."""
        e_hour_data = self.calculate_e_hour()
        if "error" in e_hour_data:
            print(f"Policy Rejected for {self.rider_id}: {e_hour_data['message']}")
            return 0.0
            
        return round(e_hour_data["e_hour"] * covered_hours, 2)
