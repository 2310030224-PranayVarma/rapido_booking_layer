# Rapido Booking Layer

A production-style backend Ride Booking System Simulator inspired by Uber/Rapido, built with **Java 17**, **Spring Boot**, **Spring Data JPA**, and **MySQL**.

The application demonstrates how a layered backend can handle ride requests, nearest-driver assignment, lifecycle transitions, validation, transaction management, centralized exception handling, and concurrency-safe driver allocation.

---

## Project Overview

This service simulates a simplified ride-booking workflow:

1. A rider requests a ride.
2. The system finds the nearest available driver.
3. The trip is created in `REQUESTED` state.
4. The trip moves through a controlled lifecycle:
   - `REQUESTED -> ACCEPTED -> STARTED -> COMPLETED`
5. The driver becomes available again after trip completion.

The project is intentionally structured like an interview-ready backend service with clean layering, DTO-based APIs, design patterns, and transactional business logic.

---

## Features

- Rider creation API
- Driver onboarding API
- Ride request API
- Nearest available driver assignment
- Driver availability tracking
- Concurrency-safe ride assignment using pessimistic locking
- Trip lifecycle management with invalid transition prevention
- REST APIs with DTO validation
- Centralized exception handling
- Structured service logging
- Focused integration and concurrency tests

---

## Architecture

The code follows a classic layered architecture:

- **Controller Layer**
  - Exposes REST endpoints
  - Validates incoming DTOs
  - Delegates business logic to services

- **Service Layer**
  - Implements use cases such as ride request and trip transitions
  - Handles transactions, logging, and orchestration
  - Applies state and strategy patterns

- **Repository Layer**
  - Uses Spring Data JPA for persistence
  - Contains a pessimistic-lock query for safe driver assignment

- **DTO Layer**
  - Separates API contracts from entities
  - Provides validation-friendly request and response models

- **Entity Layer**
  - Maps domain objects (`Rider`, `Driver`, `Trip`) to relational tables

### Architecture Diagram Explanation

```text
Client
  |
  v
Controllers  --->  DTO validation
  |
  v
Services  --->  Strategy Pattern (Driver Matching)
  |         --->  State Pattern (Trip Lifecycle)
  |         --->  Transaction Management
  v
Repositories ---> MySQL
  |
  v
Entities
```

The ride request flow enters via the controller, gets validated, then the service layer opens a transaction, locks available drivers, runs the matching strategy, creates the trip, and persists the updated state.

---

## Tech Stack

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring Validation
- MySQL
- Lombok
- JUnit 5 + Spring Boot Test
- H2 (test profile only)

---

## Database Schema

### `rider`
| Column | Type | Description |
|---|---|---|
| id | bigint | Primary key |
| name | varchar | Rider name |

### `driver`
| Column | Type | Description |
|---|---|---|
| id | bigint | Primary key |
| name | varchar | Driver name |
| latitude | double | Driver latitude |
| longitude | double | Driver longitude |
| availability | boolean | Driver availability flag |
| rating | double | Driver rating |

### `trip`
| Column | Type | Description |
|---|---|---|
| id | bigint | Primary key |
| rider_id | bigint | FK to rider |
| driver_id | bigint | FK to driver |
| status | varchar | Trip status enum |
| pickup_location | varchar | Pickup description |
| created_at | timestamp | Trip creation timestamp |

---

## Design Patterns Used

### 1. Strategy Pattern
**Classes:**
- `DriverMatchingStrategy`
- `NearestDriverMatchingStrategy`

The driver selection algorithm is abstracted behind a strategy interface so the matching logic can be replaced later with more advanced strategies such as:
- highest-rated nearest driver
- surge-aware driver allocation
- vehicle-type aware driver allocation

### 2. State Pattern
**Classes:**
- `TripStateHandler`
- `RequestedTripStateHandler`
- `AcceptedTripStateHandler`
- `StartedTripStateHandler`
- `CompletedTripStateHandler`
- `TripStateMachine`

Each trip state owns the rules for valid transitions, which keeps lifecycle rules explicit and easy to extend.

---

## Concurrency Handling Explanation

The application uses **pessimistic write locking** when fetching available drivers during ride creation:

- `DriverRepository.findAvailableDriversForUpdate()` is annotated with `@Lock(PESSIMISTIC_WRITE)`.
- `RideServiceImpl.requestRide(...)` runs inside a transaction.
- Available driver rows are locked before assignment.
- The chosen driver is marked unavailable within the same transaction.

This prevents two concurrent ride requests from assigning the same driver simultaneously.

A dedicated test (`RideServiceConcurrencyTest`) verifies that two concurrent requests competing for one driver result in:
- exactly one successful trip
- exactly one `NoAvailableDriverException`

---

## API Documentation

### Helper APIs

#### Create Rider
**POST** `/riders`

Request:
```json
{
  "name": "Pranay"
}
```

Response:
```json
{
  "id": 1,
  "name": "Pranay"
}
```

#### Create Driver
**POST** `/drivers`

Request:
```json
{
  "name": "Driver A",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "availability": true,
  "rating": 4.8
}
```

Response:
```json
{
  "id": 1,
  "name": "Driver A",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "availability": true,
  "rating": 4.8
}
```

### Required Ride APIs

#### Request Ride
**POST** `/rides/request`

Request:
```json
{
  "riderId": 1,
  "pickupLocation": "Indiranagar",
  "pickupLatitude": 12.9718,
  "pickupLongitude": 77.5941
}
```

Response:
```json
{
  "tripId": 1,
  "riderId": 1,
  "driverId": 1,
  "driverName": "Driver A",
  "status": "REQUESTED",
  "pickupLocation": "Indiranagar",
  "createdAt": "2026-05-14T23:00:00"
}
```

#### Accept Ride
**PUT** `/rides/{id}/accept`

Response:
```json
{
  "tripId": 1,
  "riderId": 1,
  "driverId": 1,
  "driverName": "Driver A",
  "status": "ACCEPTED",
  "pickupLocation": "Indiranagar",
  "createdAt": "2026-05-14T23:00:00"
}
```

#### Start Ride
**PUT** `/rides/{id}/start`

Response status moves to `STARTED`.

#### Complete Ride
**PUT** `/rides/{id}/complete`

Response status moves to `COMPLETED`, and the driver becomes available again.

#### Get Available Drivers
**GET** `/drivers/available`

Response:
```json
[
  {
    "id": 2,
    "name": "Driver B",
    "latitude": 12.98,
    "longitude": 77.61,
    "availability": true,
    "rating": 4.9
  }
]
```

---

## Validation and Error Handling

Validation is applied on incoming DTOs using Bean Validation annotations.

Centralized exception handling is implemented with `@RestControllerAdvice` for:
- resource not found errors
- no available driver conflicts
- invalid trip transitions
- DTO validation failures
- unexpected server errors

Sample error response:
```json
{
  "timestamp": "2026-05-14T23:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Trip in REQUESTED state can only move to ACCEPTED",
  "path": "/rides/1/start",
  "validationErrors": []
}
```

---

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8+

### 1. Create MySQL database
```sql
CREATE DATABASE rapido_booking;
```

### 2. Configure environment variables
```bash
export DB_URL='jdbc:mysql://localhost:3306/rapido_booking?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME='root'
export DB_PASSWORD='root'
```

### 3. Run the application
```bash
mvn spring-boot:run
```

### 4. Run tests
```bash
mvn test
```

---

## Folder Structure

```text
src
├── main
│   ├── java/com/rapido/booking
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── exception
│   │   ├── repository
│   │   ├── service
│   │   ├── state
│   │   └── strategy
│   └── resources
└── test
    ├── java/com/rapido/booking
    └── resources
```

---

## Sample End-to-End Flow

1. Create a rider
2. Create multiple drivers
3. Request a ride
4. Accept the ride
5. Start the ride
6. Complete the ride
7. Query available drivers again

This simulates the full booking lifecycle with driver availability recovery.

---

## Future Improvements

- Support driver acceptance by authenticated driver identity
- Add geospatial queries with MySQL spatial indexing
- Add ETA estimation and fare calculation
- Add ride cancellation flows
- Add pagination and filtering to driver APIs
- Add OpenAPI/Swagger documentation
- Add Redis-based queueing and distributed locking for scale
- Add observability with metrics and tracing
- Add authentication and authorization

---

## Test Coverage Summary

The repository includes focused tests for:
- nearest-driver selection through the REST API
- valid ride lifecycle transitions
- invalid lifecycle transition rejection
- concurrency-safe single-driver assignment under parallel requests

