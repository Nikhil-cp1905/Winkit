
class LocationRiskService:
    def __init__(self):
        # Phase 1: The Cold-Start Static Matrix
        self.zone_matrix = {
            "OMR_Tech_Park": 0.00,   
            "Potheri_GST": 0.30,     
        }
        self.default_v_zone = 0.15

    def calculate_v_zone(self, zone_name: str) -> float:
        """Phase 1: Static Lookup for the cold-start problem."""
        return self.zone_matrix.get(zone_name, self.default_v_zone)

    def calculate_empirical_v_zone(self, zone_id: str, current_weather_severity: float) -> float:
        """
        Phase 2 (Hyper-Localization): Calculates V_zone using our proprietary 
        delivery failure data rather than static geographical assumptions.
        """
        # In a real production environment, this queries the SQLite database:
        # SELECT COUNT(claims) / COUNT(total_deliveries) WHERE zone = zone_id AND weather > severity
        
        # MOCK DATA FOR DEMONSTRATION:
        mock_historical_data = {
            "Potheri_GST": {"total_deliveries_in_storms": 500, "claims_paid": 125},
            "OMR_Tech_Park": {"total_deliveries_in_storms": 800, "claims_paid": 16}
        }
        
        zone_history = mock_historical_data.get(zone_id)
        
        if not zone_history:
            return self.default_v_zone # Fallback if no historical data exists
            
        failure_rate = zone_history["claims_paid"] / zone_history["total_deliveries_in_storms"]
        
        # The failure rate becomes the exact geospatial penalty
        return round(failure_rate, 3)

if __name__ == "__main__":
    locator = LocationRiskService()
    
    print("--- PHASE 1: Static Penalty ---")
    print(f"Potheri Risk: {locator.calculate_v_zone('Potheri_GST')}")
    
    print("\n--- PHASE 2: Empirical Hyper-Localization ---")
    empirical_risk = locator.calculate_empirical_v_zone('Potheri_GST', current_weather_severity=0.85)
    print(f"Potheri True Historical Risk: {empirical_risk} (125 claims / 500 deliveries)")
