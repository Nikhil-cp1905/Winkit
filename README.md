# Winkit — Parametric Insurance for the Q-Commerce Workforce

### Guidewire DevTrails 2026 Submission

Winkit is a **parametric micro-insurance platform** designed for India's gig-economy delivery workforce (Blinkit, Zomato, Zepto).

The system automatically compensates workers for **income loss caused by external disruptions** such as:

- extreme weather
- government movement restrictions
- dark store outages
- telecom network failures

Unlike traditional insurance systems, Winkit uses **automated trigger detection and parametric payouts**, eliminating the need for manual claim processing.

The platform combines:

- **AI-based disruption prediction**
- **actuarial premium modeling**
- **real-time event verification**
- **zero-touch payouts**

---

# Target Persona

The system is designed for **Q-Commerce delivery workers**, who operate on **hourly or shift-based earnings**.

Example worker profile:

| Attribute | Example |
|---|---|
Platform | Blinkit |
Primary Dark Store | Sector 4 Warehouse |
Working Hours | Fri 6PM – 11PM |
Avg Earnings | ₹150/hour |

If an external disruption prevents them from completing shifts, **their income drops to zero**.

Traditional insurance models cannot handle this scenario due to **small payout size and high claim processing cost**.

---

# External Disruptions Covered

The system focuses only on **external, uncontrollable disruptions**.

## Environmental Events

- Extreme heat (Heat Index threshold)
- Heavy rainfall
- Cyclones
- Severe thunderstorms
- Flooding
- Low visibility (Fog / Smog)
- Hazardous AQI

## Infrastructure Events

- Dark store power failure
- Warehouse fire / electrical failure
- Telecom network outage

## Traffic / Urban Events

- Major road closures
- Accident hotspots
- Severe congestion
- Construction blockages
- VIP movement restrictions

## Regulatory / Government Restrictions

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

---

# Parametric Insurance Framework

Winkit implements a **parametric insurance model**, where payouts are triggered automatically when predefined conditions occur.

Example trigger:
`IF HeatIndex > 40°C
AND Worker Shift Active
THEN Auto-Payout`

No claim filing is required.

---

# Dynamic Premium Engine

Traditional insurance uses:
`Premium = Expected Loss + Margin`
Where:
`Expected Loss = p × L`

p = probability of disruption  
L = payout amount

---

## Winkit Enhancement

Gig workers have **variable shifts**, so risk must be **shift-weighted**.

Weekly premium is calculated as:
`E(Loss) = (Pweather × V)+(Psocial × V)+(Pinfra × V)`


Where:

- `V` = value of shift income
- `P` = predicted probability of disruption

Final weekly premium:
`Weekly Premium = (E(Loss) × Mfraud) + Base Margin`


Fraud multiplier increases premiums for stores with **suspicious outage history**.

---

# AI Risk Prediction Model

The backend trains an ML model using features such as:

| Feature | Description |
|---|---|
Weather Forecast | 7-day weather prediction |
Store Reliability | historical uptime |
Traffic Patterns | congestion data |
Social Events | protest frequency |
Location Risk | flood zones |

Model candidates:

- Random Forest
- XGBoost
- Gradient Boosting

Output:
`Disruption Probability Vector
[Pweather, Psocial, Pinfra]`

---

# Agentic AI Event Verification

The platform uses **LangChain Agent Executors** to verify disruption triggers.

The AI agent queries multiple external tools.

| Tool | Purpose |
|---|---|
Weather API | heat index calculation |
Traffic API | road closures |
Government Advisory API | Section 144 polygons |
Network API | telecom outages |

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

