import os
import sys
import json
import requests
import feedparser

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

try:
    from config import GEMINI_API
except ImportError:
    print("Error: Could not find GEMINI_API in config.py")
    sys.exit(1)

def fetch_live_chennai_headlines(max_headlines: int = 5) -> list:
    """Fetches the latest breaking news headlines for Chennai."""
    rss_url = "https://timesofindia.indiatimes.com/rssfeeds/2950623.cms"
    
    try:
        feed = feedparser.parse(rss_url)
        headlines = []
        
        # Grab the top N most recent articles
        for entry in feed.entries[:max_headlines]:
            clean_summary = entry.summary.split('<')[0] # Strips out any HTML tags
            headlines.append(f"{entry.title} - {clean_summary}")
            
        return headlines
    except Exception as e:
        print(f"Failed to fetch live news: {e}")
        return ["No local news available at this time."]

class CivicRiskAgent:
    def __init__(self, api_key: str = GEMINI_API):
        self.api_key = api_key
        self.endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    def analyze_civic_risk(self, news_headlines: list, rider_zone: str = "Chennai") -> dict:
        """Passes local news to the LLM and applies Urban Routing heuristics."""
        prompt = (
            "You are a risk analysis AI for a Q-commerce insurance platform in Chennai.\n"
            f"Review the following local news headlines: {news_headlines}\n"
            "Calculate the probability (0.0 to 1.0) that these events will physically prevent "
            "delivery riders from working today (e.g., severe riots, curfews, total road blocks). "
            "Ignore standard traffic jams or minor political rallies.\n"
            'You MUST respond in strict JSON format ONLY: {"p_civic": 0.XX, "event_location": "Name of specific area or None", "reason": "brief explanation"}'
        )

        headers = {"Content-Type": "application/json"}
        payload = {"contents": [{"parts": [{"text": prompt}]}]}

        try:
            response = requests.post(
                f"{self.endpoint}?key={self.api_key}",
                headers=headers,
                json=payload
            )
            response.raise_for_status()
            
            result = response.json()
            content = result["candidates"][0]["content"]["parts"][0]["text"]
            clean_content = content.replace("```json", "").replace("```", "").strip()
            
            raw_data = json.loads(clean_content)
            
            # URBAN ROUTING HEURISTICS (REALITY FILTER)
            p_raw = raw_data.get("p_civic", 0.0)
            event_location = raw_data.get("event_location", "").lower()
            rider_zone_lower = rider_zone.lower()

            # Noise Reduction Filter 
            if p_raw <= 0.15:
                p_civic = 0.02
            else:
                p_civic = p_raw

            # Epicenter Multiplier (Proximity Weighting)
            # Standardize checking to see if the zone name is inside the LLM's event location string
            if rider_zone_lower in event_location:
                p_civic = p_civic * 1.5

            # Urban Routing Cap (The Alternative Path Theorem)
            p_final = min(p_civic, 0.40)

            # Overwrite with the corrected probability
            # raw data logging
            raw_data["p_civic"] = round(p_final, 3)
            raw_data["raw_p_civic"] = p_raw 
            
            return raw_data
            
        except Exception as e:
            print(f"Agentic AI Error: {e}")
            return {"p_civic": 0.0, "event_location": "None", "reason": "Failed to parse civic risk. Defaulting to 0."}

if __name__ == "__main__":
    agent = CivicRiskAgent()
    
    print("Fetching LIVE news from Chennai...")
    live_news = fetch_live_chennai_headlines(max_headlines=5)
    
    for i, headline in enumerate(live_news):
        print(f" {i+1}. {headline}")
        
    print("\nAnalyzing live news for civic risk in Potheri...")
    risk_assessment = agent.analyze_civic_risk(live_news, rider_zone="Potheri")
    
    print(f"\nRaw LLM Probability: {risk_assessment.get('raw_p_civic')}")
    print(f"Adjusted Final Probability: {risk_assessment.get('p_civic')}")
    print(f"Event Location: {risk_assessment.get('event_location')}")
    print(f"Agent Reasoning: {risk_assessment.get('reason')}")
