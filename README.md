# Visakha Tourism - Full-Stack Web Application

A full-stack tourism web application built with **Java 21 LTS**, **Spring Boot 3.3.4**, **MySQL 8.0**, and modern **Vanilla HTML5/CSS3/JavaScript**.

Focused on **Visakhapatnam (Vizag)** and neighboring Eastern Ghats travel regions (Araku Valley, Bheemunipatnam, Borra Caves, Lambasingi, Ananthagiri), the platform provides two distinct, secure interfaces:
1. **Public Website**: Interactive destination discovery, 55+ verified places, server-side pagination with initial 20 cards and dynamic *Load More*, instant search, category filtering, Haversine-computed nearby attractions, turn-by-turn Google Maps directions, and user registration with email OTP verification.
2. **Admin Dashboard**: Role-protected management console with real database metrics, destination CRUD, publication status toggles, and photo upload management.

---

## Architecture Overview

```
VishakaTourismApp/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/visakhatourism/
│       │   │   ├── config/          # WebConfig (Static resources & CORS)
│       │   │   ├── controller/      # REST API Controllers (Auth, Destinations, Categories, Admin)
│       │   │   ├── dto/             # Request, Response, and Page DTO records
│       │   │   ├── entity/          # JPA Entities (Destination, Category, User, Photo, Otp)
│       │   │   ├── exception/       # Custom Exceptions & GlobalExceptionHandler
│       │   │   ├── repository/      # Spring Data JPA Repositories
│       │   │   ├── security/        # Spring Security, JWT Token Provider & Filter
│       │   │   └── service/         # Business Services, Haversine math, Photo & OTP logic
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/    # Flyway Migrations (V1 Schema, V2 Seed Catalog)
│       └── test/java/com/visakhatourism/
├── frontend/
│   ├── index.html                   # Coastal Homepage with Hero & Featured Places
│   ├── destinations.html            # 20 Initial Cards, Load More, Search & Filters
│   ├── destination-details.html     # Detail page with Haversine Nearby & Maps
│   ├── login.html                   # Traveler authentication
│   ├── register.html                # Registration triggering OTP
│   ├── verify-otp.html              # 6-Digit OTP verification & Countdown timer
│   ├── forgot-password.html         # OTP-based recovery request
│   ├── reset-password.html          # Password reset with OTP
│   ├── profile.html                 # Traveler profile & edit display name
│   ├── admin-login.html             # Dedicated administrator entrance
│   ├── admin-dashboard.html         # Metrics, Category breakdown, Recent records
│   ├── admin-destinations.html      # Table CRUD, status toggle, search, pagination
│   ├── admin-destination-form.html  # Destination add/edit + Photo manager
│   ├── css/
│   │   ├── styles.css               # Coastal design system (Grid, Flexbox, Toast, Lightbox)
│   │   └── admin.css                # Admin console layout & data tables
│   └── js/
│       ├── api.js                   # Centralized Fetch API client with Bearer handling
│       ├── auth.js                  # Session tokens, guards & dynamic nav updates
│       ├── main.js                  # Homepage controller
│       ├── destinations.js          # Catalog controller (Load More & Pagination)
│       ├── destination-details.js   # Details controller & Gallery lightbox
│       └── admin.js                 # Admin dashboard & management controller
├── uploads/destinations/            # Persistent local photo storage
├── .env.example                     # Environment template
└── README.md
```

---

## Technology Stack

- **Backend**: Java 21 LTS, Spring Boot 3.3.4, Spring Web, Spring Data JPA, Spring Security, Spring Boot Validation, Spring Boot Mail
- **Security**: JWT (jjwt 0.12.6) Bearer tokens, BCrypt password hashing, role-based authorization (`ROLE_USER`, `ROLE_ADMIN`)
- **Database**: MySQL 8.0, Flyway database migrations (`V1__init_schema.sql`, `V2__seed_categories_and_destinations.sql`)
- **Frontend**: HTML5, CSS3 (CSS Grid, Flexbox, custom coastal variables), Vanilla JavaScript ES6 Modules, Fetch API
- **Maps**: Google Maps URLs with verified coordinates (`https://www.google.com/maps/dir/?api=1&destination=LAT,LNG`)
- **Nearby Calculations**: Geodesic distance using the **Haversine formula** in kilometers

---

## Destination Catalog

The database is pre-seeded via Flyway with **55 verified destinations** across Visakhapatnam, Bheemili, Araku Valley, and Ananthagiri Hills:
1. Ramakrishna Beach (RK Beach)
2. Kailasagiri Hilltop Park
3. Rushikonda Beach (Blue Flag certified)
4. Yarada Beach
5. Sri Varaha Lakshmi Narasimha Temple (Simhachalam)
6. INS Kursura Submarine Museum
7. TU-142 Aircraft Museum
8. Sea Harrier Museum
9. Indira Gandhi Zoological Park
10. Tenneti Park
11. Dolphin's Nose & Lighthouse
12. Borra Caves (Ananthagiri)
13. Araku Valley Tribal Museum
14. Katiki Waterfalls
15. Chaparai Water Cascades
16. Thotlakonda Buddhist Complex
17. Bavikonda Buddhist Heritage Site
18. Bojjannakonda Rock-Cut Monasteries
19. Kondakarla Ava Freshwater Lake & Bird Sanctuary
20. Kambalakonda Wildlife Sanctuary
21. Bheemili Beach & Dutch Town
22. VMRDA City Central Park
23. Visakha Museum (Dutch Bungalow)
24. Ross Hill Church
25. Sri Kanaka Mahalakshmi Temple
... and 30 additional verified coastal, hill, and heritage destinations!

---

## Getting Started

### 1. Prerequisites
- **Java 21 LTS** (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **MySQL Server 8.0** running on `localhost:3306`

### 2. Database Setup
Create the MySQL database:
```sql
CREATE DATABASE IF NOT EXISTS visakha_tourism CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
Flyway automatically creates all tables and seeds the 55 destinations and 7 categories on first startup.

### 3. Environment Configuration
Copy `.env.example` to your environment or update `backend/src/main/resources/application.yml`:
```properties
DB_URL=jdbc:mysql://localhost:3306/visakha_tourism?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=YOUR_MYSQL_PASSWORD
JWT_SECRET=d41d8cd98f00b204e9800998ecf8427e9a82f34e6b1c85d72049e3fa185bc029a174829375b48194e823758194a73b21
```

### 4. Running the Application
From the workspace root:
```bash
mvn -f backend/pom.xml spring-boot:run
```
The application will start on: **`http://localhost:8080/`**

Navigating to `http://localhost:8080/` loads the frontend directly. Both frontend assets and REST APIs are served seamlessly.

---

## Administrator & Contact Information
- **Administrator**: Siva Kumar Allu
- **Email**: `sivakumarallu4@gmail.com`
- **Phone**: `6281429422`
- **Location**: Gajuwaka, Visakhapatnam, Andhra Pradesh 530026
- **Portal URL**: [http://localhost:8080/admin-login.html](http://localhost:8080/admin-login.html)
- **Default Password**: `Admin@Vizag2026`

---

## Curated Itineraries & Budget Management

The platform includes a dedicated **Smart Tour Planner & Budget Management System** at `http://localhost:8080/itineraries.html` featuring:
1. **2-Day Plan (Coastal & Heritage Weekend)**: Linear Beach Road corridor + Bheemili Marine Highway.
2. **3-Day Plan (Coast, Sacred Hills & Karst Caves)**: Adds Simhachalam hilltop temple + Borra Caves excursion.
3. **4-Day Plan (Coast & Araku Valley Highland Expedition)**: Adds Vistadome train ride, Katiki waterfalls jeep trek, tribal museum, and Araku valley overnight stay.
4. **5-Day Plan (The Grand Vizag Expedition)**: Adds Kondakarla Ava freshwater lotus catamaran ride, 4th-century Bojjannakonda Buddhist rock-cut caves, and Dolphin's Nose Lighthouse.

### Key Features:
- **Strict Geographic Sequencing**: Places are visited along linear natural corridors, saving 3–4 hours of daily travel time.
- **Interactive Budget Estimator**: Dynamic calculator supporting 3 tiers (*Budget*, *Mid-Range*, *Luxury*) across Solo, Couple, and Family group sizes with itemized breakdowns (Hotels, Fuel/Transit, Dining, Entry Tickets).
- **Transit Advice**: Tailored vehicle recommendations (Scooter for Beach Road, Vistadome train for valley, 4x4 Jeep for Katiki falls, private cab for ghat roads) with road conditions and safety warnings.

---

## Email OTP Delivery & Authentication

The application is configured to deliver **real 6-digit OTP emails** directly to users' inboxes using Google SMTP:
- **Sender Address**: `sivakumarallu4@gmail.com`
- **SMTP Host**: `smtp.gmail.com` (Port `587` with STARTTLS)
- **Authentication**: Authenticated via 16-character Google App Password (configured in `.env` / environment variable `MAIL_PASSWORD`)
- **Sender Name**: `Visakha Tourism`
- **Supported Flows**:
  - **Account Registration Verification**: Sent immediately upon signup at [http://localhost:8080/register.html](http://localhost:8080/register.html)
  - **Password Reset**: Sent upon password reset request at [http://localhost:8080/forgot-password.html](http://localhost:8080/forgot-password.html)
  - **OTP Resend**: Instant resend with 60-second cooldown protection
- **Email Design**: Responsive HTML template with emerald-blue branding, 5-minute expiry warning, security advice, and official support contact information.
- **Safety Fallback**: If network or SMTP credentials ever fail, the application catches the error, logs a secure fallback OTP in application logs, and protects the system from crashing.

---

## Key REST API Endpoints

### Public Destination APIs
- `GET /api/destinations?page=0&size=20&search=...&categoryId=...&sort=...` — Paged destinations
- `GET /api/destinations/{slug}` — Full destination details
- `GET /api/destinations/featured` — Featured highlights
- `GET /api/destinations/{id}/nearby?limit=4` — Geodesically sorted nearby attractions
- `GET /api/destinations/{id}/photos` — Gallery photos
- `GET /api/categories` — Destination categories

### Authentication APIs
- `POST /api/auth/register` — Initiate registration & trigger OTP
- `POST /api/auth/verify-email` — Verify 6-digit OTP and activate account
- `POST /api/auth/resend-otp` — Resend OTP (with 60-second cooldown)
- `POST /api/auth/login` — Traveler login (unverified users blocked)
- `POST /api/auth/admin-login` — Dedicated administrator login
- `GET /api/auth/me` — Authenticated user profile
- `PUT /api/auth/profile` — Update traveler profile
- `POST /api/auth/forgot-password` — Password reset OTP request
- `POST /api/auth/reset-password` — Set new password with OTP

### Admin APIs (`ROLE_ADMIN` required)
- `GET /api/admin/dashboard` — Live database-backed metrics & category distribution
- `GET /api/admin/destinations` — Management table with filters and pagination
- `POST /api/admin/destinations` — Create destination
- `PUT /api/admin/destinations/{id}` — Update destination
- `DELETE /api/admin/destinations/{id}` — Delete destination & photos
- `PATCH /api/admin/destinations/{id}/status` — Toggle published/hidden status
- `POST /api/admin/destinations/{id}/photos` — Upload image file (Multipart)
- `POST /api/admin/destinations/{id}/photos/url` — Add photo by URL
- `DELETE /api/admin/photos/{photoId}` — Remove photo
- `PATCH /api/admin/photos/{photoId}/primary` — Set primary photo

---

## Testing & Quality Assurance

Run the test suite:
```bash
mvn -f backend/pom.xml test
```
Includes:
- Geodesic Haversine distance accuracy tests
- Nearby destination ranking by distance
- Google Maps URL generation tests
- OTP verification, expiration, and attempt rate-limiting tests
- Role authorization tests

---

## Production Build

Package as an executable JAR:
```bash
mvn -f backend/pom.xml clean package -DskipTests
java -jar backend/target/visakha-tourism-backend-1.0.0.jar
```
