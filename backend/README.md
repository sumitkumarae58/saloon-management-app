# Salon Booking System - Production-Grade Spring Boot Backend

This repository contains a secure, production-grade Spring Boot 3 enterprise application backend designed for the **Salon Booking System**. It implements a robust, high-performance, and secure Multi-Role Architecture (Super Admin, Salon Owner, Barber, and Customer) integrating relational persistence (PostgreSQL) and structured memory caching (Redis).

---

## 🚀 Key Architectural Features

- **Modern Stack**: Java 21, Spring Boot 3.2.4, Spring Data JPA, and PostgreSQL.
- **Robust Security**: Secure token-based OAuth/JWT auth (Access & Refresh loops) with Method-Level Permission Control via Spring Security annotations (`@PreAuthorize`).
- **High-Performance Caching**: Redis-backed session & slot availability caching to protect database resources.
- **Auto-allocation Engine**: Smart "Any Stylist" scheduling logic that dynamically allocates available barbers without double-booking or slot conflicts.
- **Auditable Schemas**: Automated, versioned database migrations managed via Flyway.
- **Comprehensive API Documentation**: Clean OpenAPI/Swagger 3 endpoint catalog.

---

## 🛠️ Project Directory Layout

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/salonbooking/
│   │   │   ├── SalonBookingApplication.java # Bootstrap Entry Point
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java       # RBAC, Stateless Sessions, CORS
│   │   │   │   ├── RedisConfig.java          # Redis Client configurations
│   │   │   │   └── SwaggerConfig.java        # Swagger UI open specifications
│   │   │   ├── controller/
│   │   │   │   ├── auth/AuthController.java  # Register & Login REST endpoints
│   │   │   │   ├── admin/AdminController.java# Salon Suspension, Analytics
│   │   │   │   ├── owner/OwnerController.java# Salon profile and Barber affiliation
│   │   │   │   ├── barber/BarberController.java # Stylist schedules and leave periods
│   │   │   │   └── customer/CustomerController.java # Booking flows and discovery
│   │   │   ├── service/                      # Core business controllers
│   │   │   ├── repository/                   # Spring Data database layers
│   │   │   ├── entity/                       # JPA database model representations
│   │   │   ├── dto/                          # Clean data transfer mappings
│   │   │   └── exception/                    # Global Exception handler mappings
│   │   └── resources/
│   │       ├── application.yml               # Database & Secret properties
│   │       └── db/migration/
│   │           └── V1__init_schema.sql       # Versioned Flyway DB migrations
│   └── test/                                 # Unit & Integration tests
├── pom.xml                                   # Maven dependency manifests
└── docker-compose.yml                        # Docker Postgres & Redis environments
```

---

## 💾 Local Environment Initialization

### 1. Prerequisite Requirements
Ensure you have the following installed locally:
- **Java JDK 21**
- **Maven**
- **Docker & Docker Compose**

### 2. Stand up PostgreSQL & Redis
Launch the pre-configured local development database and memory cache container services:
```bash
docker-compose up -d
```
This boots up:
- **PostgreSQL**: Accessible at `localhost:5432` (DB: `salon_db`, User: `salon_user`, Password: `salon_password`)
- **Redis**: Accessible at `localhost:6379`

### 3. Build & Compile the Application
Ensure all dependencies download successfully and compile:
```bash
mvn clean compile
```

### 4. Run the Backend App
Start the Spring Boot container engine:
```bash
mvn spring-boot:run
```
The application bootstraps, connects to Postgres, executes Flyway migrations, connects to Redis, and begins listening for requests on port `8080`.

---

## 🛡️ Secure Authentication Flow (JWT)

Endpoints under `/api/customer`, `/api/barber`, `/api/owner`, and `/api/admin` require a valid JWT token passed in the Request Header:

```http
Authorization: Bearer <your_jwt_access_token>
```

1. **New Users**: Submit a registration request to `POST /api/auth/register` specifying role flags (e.g. `ROLE_CUSTOMER`, `ROLE_SALON_OWNER`, `ROLE_BARBER`).
2. **Login**: Submit credentials to `POST /api/auth/login` to obtain an `accessToken` (valid for 15 minutes) and a `refreshToken` (valid for 7 days).

---

## 📑 Live OpenAPI / Swagger Docs

Once the Spring Boot application is running, open your web browser and navigate to:
```
http://localhost:8080/swagger-ui/index.html
```
This opens the interactive Swagger Console where you can visually inspect all available endpoints and execute live API requests.

---

## 📱 Seamless Integration with the Android App

This backend's contract maps directly with our Jetpack Compose Android client database models (`SalonEntity`, `StaffEntity`, `ServiceEntity`, `BookingEntity`). 
- **Retrofit Configuration**: In the Android client, swap local SQLite/Room services for a Retrofit/Ktor network client targeting `http://localhost:8080/api/customer`.
- **JWT Storage**: Store the obtained access token securely in the Android client using **EncryptedSharedPreferences** or Jetpack **DataStore**. Inject it dynamically as an interceptor header for downstream requests.
