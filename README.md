# Winkit — Parametric Insurance for the Q-Commerce Workforce

### Guidewire DevTrails 2026 Submission

Winkit is a **parametric micro-insurance platform** designed for India's gig-economy delivery workforce **(Blinkit/Zepto/Swiggy Instamart)**.
It automatically compensates workers for income loss caused by external disruptions (extreme weather, civic unrest) using real-time data and zero-touch smart contracts.

# Target Persona
Our target use is Q-commerce delivery rider operating on variable and shift-based earnings. If a sudden disruption occurs—like severe flooding or a government-mandated curfew—their income drops to zero. Traditional insurance cannot serve this demographic because the administrative cost of processing a ₹500 missed-shift claim is higher than the payout itself.

## The Scenario
A rider operating near Potheri, Chennai, plans to work a 6-hour shift. The weather forecast is clear, but a sudden political protest severely blocks the main GST Road. The rider is physically unable to safely deliver orders.

## End-to-End Workflow
1. **App Initialization:** The rider opens the Winkit app. The backend calculates their dynamic "Earning Velocity" (e.g., ₹150/hr) based on historical delivery ledger data.

2. **Real-Time Risk Assessment:** The system instantly queries Weather APIs and our Agentic LLM (which reads live local RSS news feeds) to assess the probability of disruption.

3. **Smart Quote:** The rider purchases a risk-adjusted micro-policy for a week.

4. **Parametric Trigger:** Later that day, the Agentic AI confirms via news APIs that the road is fully blocked, hitting the disruption threshold.

5. **Zero-Touch Payout:** Because the parametric condition is mathematically met, the system monitors the rider. Based on the number of deliveries that the rider received for that area and the amount of time that was required to take de-tour. By the end of shift, Winkit instantly deposits the ₹1500 (1 hours × ₹150) coverage into the rider's wallet. No claims adjusters, no manual verification.

# Weekly Premium Model and Parametric Triggers
Winkit eliminates manual claim processing by tying payouts to objective, third-party data thresholds. The basic formula for this insurance policy is

$$
\text{Pure Premium} = max(p_{weather},p_{civic}) \times L
$$

where \
$p_{weather}$ : the probability of precipitation or harsh weather  \
$p_{civic}$ : riot, political movement ans other similar event \
$L$ : payout

To implement the model, we have dvided our implementation in 2 stages.
## Stage 1 - C.O.L.D. Start
Stage 1 establishes the security layer for the Winkit ecosystem through robust anomaly and fraud detection. By implementing a Risk Multiplier, we create a data-driven barrier against bad actors attempting to exploit threshold-based payouts. This "Cold Start" integrity is a prerequisite for the scaling logic introduced in Stage 2.

$$
\beta = 1.0 + U_{weather} + F_{risk} + V_{zone}
$$

where \
$U_{weather}$ : unpredictability of weather \
$F_{risk}$ : game risk, where all the users are penalised. \
$V_{zone}$ : penalizing less developed area where chance of impact is higher. \

### $U_{weather}$

This is dependent on 2 values, binary variance and time decay. 
We calculate the binary variance using bernoulli distribution where,
**Binary Variance** \
{variance} = p(1-p)\

This value peaks to 0.25 when p = 0.5.

**Time Decay**
A forecast for tomorrow is highly reliable but the same could not be said for 5 days in future. We are calculating with with the help of theta decay.
\ k&radic;t \

Finally, $U_{weather} = k&radic;t + w.p(1-p)$ \
here, \
t = no. of days in the future \
k = time constant(0.05) \
p = pop(probability of precipitation) \
w = variance weight constant 0.05 \
If a pre-agreed API threshold is crossed the policy executes automatically.

# Platform Justification (Mobile-First)
Winkit is strictly deployed as a native Android Mobile Application (Kotlin + Jetpack Compose). A web app cannot support our hyper-local risk engine, which requires continuous GPS Telemetry and Background location access to accurately map the rider to a specific 174m hexagonal grid for dynamic geospatial pricing.

# Integrating AI/ML (Premium Calculation & Fraud Detection)
Winkit utilizes AI/ML across three distinct layers of the architecture to ensure accurate pricing and eliminate systemic abuse:

- **Dynamic Premium Calculation (The β Multiplier):** The premium relies on β=1.0+Urisk​+Frisk​+Vzone​. As the platform scales, Vzone​ (geospatial penalty) transitions from static data to an empirical ML feedback loop. By mapping operational zones using Uber H3 Hexagonal Grids, the system tracks historical delivery failure rates per hexagon. If a specific street floods repeatedly, the algorithm automatically spikes the Vzone​ premium for that exact grid.

- **Automated Fraud Detection (Frisk​):** Because the platform is 100% parametric, individual "fake claims" are impossible (a rider cannot claim it rained if the API says it is sunny). However, to protect the capital pool from systemic platform abuse, we include Frisk​, a dynamic fraud buffer that algorithmically scales based on suspicious user clustering or historically anomalous API data.

- **Agentic AI for Unstructured Risk:** Weather provides structured probabilities, but civic risk (riots, curfews) does not. We integrate a lightweight Agentic AI layer utilizing the Gemini 2.5 Flash API. By parsing live local RSS news feeds through strict zero-shot prompts, the LLM acts as an extraction agent, returning a deterministic JSON probability of civic disruption to feed the math engine.


# External Disruptions Covered

The system focuses only on **external, uncontrollable disruptions**.

### Environmental Events

- Extreme heat (Heat Index threshold)
- Heavy rainfall
- Cyclones
- Severe thunderstorms
- Flooding
- Low visibility (Fog / Smog)
- Hazardous AQI

### Infrastructure Events

- Dark store power failure
- Warehouse fire / electrical failure
- Telecom network outage

### Traffic / Urban Events

- Major road closures
- Accident hotspots
- Severe congestion
- Construction blockages
- VIP movement restrictions

### Regulatory / Government Restrictions

- Section 144 curfews
- Emergency lockdowns
- Election restrictions
- Festival crowd control zones
- Protest zones

---

# Events NOT Covered

To prevent **moral hazard**, the following disruptions are excluded:

- vendor strikes
- supply chain stockouts
- demand collapse
- voluntary worker absence


Example reasoning pipeline:
`User Location
↓
Check GeoJSON Government Restriction Zones
↓
Verify Weather and Traffic Data
↓
Validate Disruption Event
↓
Trigger Automatic Payout`

---

# Paid Relocation Engine

If a **dark store becomes unavailable**, the system calculates the **nearest alternative store**.

The system uses the **A* path planning algorithm**.

Graph nodes represent:

- road intersections
- active dark stores

Edge weights represent:

- travel time
- traffic conditions

Computation:
`Shortest Path → Alternate Dark Store`

The worker receives **paid transit compensation** for relocation time.

---

# System Architecture
`Mobile App
↓
API Gateway (FastAPI)
↓
Event Detection Layer
↓
AI Risk Prediction Engine
↓
Trigger Validation (LangChain Agents)
↓
Payout Engine
↓
Wallet Service`


Core infrastructure components:

- FastAPI backend
- PostgreSQL + PostGIS
- ML prediction service
- LangChain agent system
- notification pipeline

---

# Mobile Application (Worker App)

Developed using **Kotlin + Jetpack Compose**.

Core features:

- shift registration
- dynamic weekly premium pricing
- real-time disruption monitoring
- automated payout notifications

---

# Insurer Dashboard

Built with **Next.js + React**.

Features include:

- disruption analytics
- payout monitoring
- geospatial event visualization
- fraud detection insights

---

# Tech Stack

| Layer | Technology |
|---|---|
Mobile | Kotlin + Jetpack Compose |
Backend | FastAPI |
Database | PostgreSQL + PostGIS |
ML | Scikit-Learn |
AI Agents | LangChain |
Routing Engine | NetworkX |
Web Dashboard | Next.js |
Maps | Mapbox / Leaflet |
Notifications | Firebase Cloud Messaging |

---

# Implementation Plan

## Phase 1

- UI wireframes
- premium calculation engine
- basic ML disruption prediction
- trigger simulation

## Phase 2

- agentic validation system
- relocation engine
- dashboard analytics

## Phase 3

- real-time data integration
- production-ready demonstration

---

# Team

**Astro Bugs**

| Member | Role |
|---|---|
Pavithra | Mobile Architecture |
Ayush | AI/ML & Risk Modeling |
Nikhil | Web Dashboard |
Amman | Product Strategy |

---

