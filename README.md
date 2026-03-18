# Winkit — Parametric Insurance for the Q-Commerce Workforce

### Guidewire DevTrails 2026 Submission

Winkit is a **parametric micro-insurance platform** designed for India's gig-economy delivery workforce **(Blinkit/Zepto/Swiggy Instamart)**.
Winkit automatically compensates gig workers for income loss caused by external disruptions, such as extreme weather and civic unrest, using **real-time data** and **zero-touch smart contracts**. We are deploying the platform in strategic stages. The first stage establishes a secure foundation by focusing on **data collection, anomaly detection, and algorithmic fraud prevention**. This proprietary **data moat powers** our second stage: implementing advanced AI and **H3 Hexagonal Hierarchical Geospatial Indexing to hyper-localize risk across the operational zone**. By continuously **analyzing real-time news, weather updates, and traffic telemetry** within these spatial grids, Winkit maps the exact **physical reality of the streets** to **instantly trigger zero-touch claims.**

# Target Persona
Our target use is Q-commerce delivery rider operating on variable and shift-based earnings. If a sudden disruption occurs—like severe flooding or a government-mandated curfew—their income drops to zero. Traditional insurance cannot serve this demographic because the administrative cost of processing a ₹500 missed-shift claim is higher than the payout itself.

## The Scenario
A rider operating near Potheri, Chennai, plans to work a 6-hour shift. The weather forecast is clear, but a sudden political protest severely blocks the main GST Road. The rider is physically unable to safely deliver orders.

## End-to-End Workflow
1. **App Initialization:** The rider opens the Winkit app. The backend calculates their dynamic "Earning Velocity" (e.g., ₹150/hr) based on historical delivery ledger data.

2. **Real-Time Risk Assessment:** The system instantly queries Weather APIs and our Agentic LLM (which reads live local RSS news feeds) to assess the probability of disruption.

3. **Smart Quote:** The rider purchases a risk-adjusted micro-policy for a week.

4. **Parametric Trigger:** Later that day, the Agentic AI confirms via news APIs that the road is fully blocked, hitting the disruption threshold.

5. **Zero-Touch Payout:** Because the parametric condition is mathematically met, the system monitors the rider. Based on the number of deliveries that the rider received for that area and the amount of time that was required to take de-tour. By the end of shift, Winkit instantly deposits the ₹150 (1 hours × ₹150) coverage into the rider's wallet. No claims adjusters, no manual verification.

# Weekly Premium Model and Parametric Triggers
Winkit eliminates manual claim processing by tying payouts to objective, third-party data thresholds. The basic formula for this insurance policy is

$$
\text{Pure Premium} = max(p_{weather},p_{civic}) \times L
$$

where, 

$p_{weather}$ : the probability of precipitation or harsh weather  \
$p_{civic}$ : riot, political movement ans other similar event \
$L$ : payout

To implement the model, we have divided our implementation in 2 stages.
## Stage 1 - C.O.L.D. Start
Stage 1 establishes the security layer for the Winkit ecosystem through robust anomaly and fraud detection. By implementing a Risk Multiplier, we create a data-driven barrier against bad actors attempting to exploit threshold-based payouts. This "Cold Start" integrity is a prerequisite for the scaling logic introduced in Stage 2.

$$
\beta = 1.0 + U_{weather} + F_{risk} + V_{zone}
$$

where, \
$U_{weather}$ : unpredictability of weather \
$F_{risk}$ : game risk, where all the users are penalised based on history. New users are not penalized. \
$V_{zone}$ : penalizing less developed area where chance of disruption impact is high. 


### $U_{weather}$
This is dependent on 2 values, binary variance and time decay. 
We calculate the binary variance using bernoulli distribution where,

**Binary Variance** 

$variance = p(1-p)$

This value peaks to 0.25 when p = 0.5.

**Time Decay**

A forecast for tomorrow is highly reliable but the same could not be said for 5 days in future. We are calculating with with the help of theta decay = $\ k&radic;t \$

Finally, 

$$U_{weather} = k&radic;t + w.p(1-p)$$ 

here, \
$t$ = no. of days in the future \
$k$ = time constant(0.05) \
$p$ = pop(probability of precipitation) \
$w$ = variance weight constant 0.05 \
If a pre-agreed API threshold is crossed the policy executes automatically.

The final premium payed by the user in the first stage is

$$
\text{Premium} = (max(p_{weather},p_{civic}) \times L  \times \beta) + Platform fee
$$

## Stage 2 - Hyper-Localization & AI Data Moat
As the platform scales, the β multiplier transitions from static data to an empirical ML feedback loop.
- H3 Spatial Mapping: By mapping operational zones using Uber H3 Hexagonal Hierarchical Geospatial Indexing to hyper-localize risk across the operational zone, the system tracks historical delivery failure rates. If a specific street floods repeatedly, the algorithm automatically spikes the Vzone​ premium for that exact grid.
  
- Agentic AI: Weather provides structured probabilities, but civic risk (riots, curfews) does not. We integrate a lightweight Agentic AI layer utilizing the Gemini 2.5 Flash API. By parsing live local RSS news feeds through strict zero-shot prompts, the LLM returns a deterministic JSON probability of civic disruption to feed the math engine.

New users for whom no data is available are given a default score of the zone average.

# Integrating AI/ML
Winkit utilizes AI/ML across three distinct layers of the architecture to ensure accurate pricing and eliminate systemic abuse:

- **Dynamic Premium Calculation (The β Multiplier):** The premium relies on β=1.0+Urisk​+Frisk​+Vzone​. As the platform scales, Vzone​ (geospatial penalty) transitions from static data to an empirical ML feedback loop. By mapping operational zones using Uber H3 Hexagonal Grids, the system tracks historical delivery failure rates per hexagon. If a specific street floods repeatedly, the algorithm automatically spikes the Vzone​ premium for that exact grid.

- **Automated Fraud Detection ($F_{risk}$​):** Because the platform is 100% parametric, individual "fake claims" are impossible (a rider cannot claim it rained if the API says it is sunny). However, to protect the capital pool from systemic platform abuse, we include $F_{risk}$​, a dynamic fraud buffer that algorithmically scales based on suspicious user clustering or historically anomalous API data.

- **Agentic AI for Unstructured Risk:** Weather provides structured probabilities, but civic risk (riots, curfews) does not. We integrate a lightweight Agentic AI layer utilizing the Gemini 2.5 Flash API. By parsing live local RSS news feeds through strict zero-shot prompts, the LLM acts as an extraction agent, returning a deterministic JSON probability of civic disruption to feed the math engine.

- 
  
# Events
## Events Covered

| Category | Event Types |
| :--- | :--- |
| **Environmental** | Heavy rainfall, Cyclones, Severe thunderstorms, Flooding, Low visibility (Fog / Smog) |
| **Traffic / Urban** | Major road closures, Accident hotspots, Severe congestion, Construction blockages, VIP movement restrictions |
| **Regulatory** | Section 144 curfews, Emergency lockdowns, Election restrictions, Festival crowd control zones, Protest zones |

- Regulatory events that have been informed well in time, before 48 hours of the disruption will not be liable in the policy
- If unplanned events continue for more than 1 week. The policy for next week will be on hold and not issued for the next week.
  
## Events NOT Covered

To prevent **moral hazard**, the following disruptions are excluded:

- vendor strikes
- supply chain stockouts
- demand collapse
- voluntary worker absence

# Platform Justification (Mobile-First)
Winkit is deployed as a native Android Mobile Application (Kotlin + Jetpack Compose). A web app cannot support our hyper-local risk engine, which requires continuous GPS Telemetry and Background location access to accurately map the rider to a specific hexagonal grid for dynamic geospatial pricing. The users are payed in their UPI bank account.

### Insurer Dashboard

Built with **Next.js + React**.

Features include:

- disruption analytics
- payout monitoring
- geospatial event visualization
- fraud detection insights

# Tech Stack

| Layer | Technology |
|---|---|
Mobile | Kotlin + Jetpack Compose |
Backend | FastAPI |
Database | PostgreSQL + PostGIS |
ML | Scikit-Learn |
AI Agents | Gemini 2.5 Flash API (Direct Structured Outputs) |
Routing Engine | NetworkX |
Web Dashboard | Next.js |
Maps | Mapbox / Leaflet |
Notifications | Firebase Cloud Messaging |
Payments | Razorpay Sandbox |


Example reasoning pipeline:
`User Location
↓
Check GeoJSON Government Restriction Zones
↓
Verify Weather and Traffic Data
↓
Validate Disruption Event
↓
Trigger Automatic Payout to register UPI`

---

# System Architecture
image to be added here

# Implementation
<img width="935" height="476" alt="Screenshot_20260318_182829" src="https://github.com/user-attachments/assets/87e1bc19-e038-420d-b3df-ea2684301e68" /> \
This is the prototype and basic working of the engine when a new user triggers the system
<img width="935" height="481" alt="Screenshot_20260318_182854" src="https://github.com/user-attachments/assets/1849b190-0f7a-412c-9c7b-dabbbdea01b7" /> \
This is the prototype and basic working of the engine when a veteran user triggers the system


The structure of our code base:
```
├── app.db
├── core-backend
│   ├── database.py
│   ├── __pycache__
│   └── worker_profile.py
├── engine
│   └── dynamic_pricing
├── frontend
│   ├── app
│   ├── build.gradle.kts
│   ├── gradle
│   ├── gradle.properties
│   ├── gradlew
│   ├── gradlew.bat
│   └── settings.gradle.kts
├── gig_workers_db.csv
├── README.md
├── services
│   ├── civic_risk_agent.py
│   ├── location_risk_service.py
│   └── weather_api_client.py
├── test_flow.py
├── trigger-workers
│   └── event_evaluator.py
└── WinkIt

```

**Core Risk Engine**: The heart of the program is `\engine`. It ingests real-time data to solve the β multiplier equation. We are yet to add the H3 Integration.

**Agentic AI Layer**: To solve the problem of unstructured civic risk, we utilize Gemini 2.5 Flash as a "Reasoning Agent."

- `civic_risk_agent.py`: This service fetches local RSS feeds and news snippets. It uses structured output (JSON) to convert news headlines into a deterministic pcivic​ value between 0.0 and 1.0.
- `weather_api_client.py`: Interacts with OpenWeather/Weatherstack to provide the $p_{weather​}$ grounding data.

**Parametric Trigger & Evaluator**: This is the zero-touch layer.

- `event_evaluator.py`: A background worker that constantly compares the pre-agreed policy thresholds against live data feeds. When a condition is met within a specific H3 hexagon, it flags all active policies in that grid for payout.

**Backend & Data Persistence**

- `database.py`: Manages the PostgreSQL/PostGIS instance.
- `worker_profile.py`: Maintains the "Earning Velocity" and $F_{risk}​ (Fraud Score) for each rider, ensuring that payouts are proportional to actual historical performance.

**Mobile Client**: Built with Kotlin and Jetpack Compose, the mobile app acts as the primary data sensor. It handles:
 - Background Telemetry: Periodically pings the backend with encrypted location data to verify the rider is within their insured risk zone.
 - Instant Wallet: Displays real-time policy status and immediate payout notifications via Firebase.
# Team

**Astro Bugs**

| Member | Role |
|---|---|
Pavithra | Mobile Architecture |
Ayush | AI/ML & Risk Modeling |
Nikhil | Web Dashboard |
Amman | Product Strategy |

---

