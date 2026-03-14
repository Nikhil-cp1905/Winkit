import os
import sys
import json
import requests

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

try:
    from config import GEMINI_API
except ImportError:
    print("Error: Could not find GEMINI_API in config.py")
    sys.exit(1)
import feedparser

def fetch_live_chennai_headlines(max_headlines: int = 5) -> list:
    """Fetches the latest breaking news headlines for Chennai."""
    rss_url = "https://timesofindia.indiatimes.com/rssfeeds/2950623.cms"
    
    try:
        feed = feedparser.parse(rss_url)
        headlines = []
        
        # Grab the top N most recent articles
        for entry in feed.entries[:max_headlines]:
            # We combine the title and a snippet of the summary for the LLM
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


    def analyze_civic_risk(self, news_headlines: list) -> dict:
        """Passes local news to the LLM to calculate the probability of a civic disruption."""
        prompt = (
            "You are a risk analysis AI for a Q-commerce insurance platform in Chennai.\n"
            f"Review the following local news headlines: {news_headlines}\n"
            "Calculate the probability (0.0 to 1.0) that these events will physically prevent "
            "delivery riders from working today (e.g., severe riots, curfews, total road blocks). "
            "Ignore standard traffic jams or minor political rallies.\n"
            'You MUST respond in strict JSON format ONLY: {"p_civic": 0.XX, "reason": "brief explanation"}'
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
            
            return json.loads(clean_content)
            
        except Exception as e:
            print(f"Agentic AI Error: {e}")
            return {"p_civic": 0.0, "reason": "Failed to parse civic risk. Defaulting to 0."}

# --- Quick Local Test ---
# --- Quick Local Test ---
if __name__ == "__main__":
    agent = CivicRiskAgent()
    
    print("Fetching LIVE news from Chennai...")
    live_news = fetch_live_chennai_headlines(max_headlines=5)
    
    for i, headline in enumerate(live_news):
        print(f" {i+1}. {headline}")
        
    print("\nAnalyzing live news for civic risk...")
    risk_assessment = agent.analyze_civic_risk(live_news)
    
    print(f"\nProbability of Disruption: {risk_assessment.get('p_civic')}")
    print(f"Agent Reasoning: {risk_assessment.get('reason')}")
