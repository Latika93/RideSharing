# Trip Lifecycle System (FSM)

This document describes the Trip Lifecycle system implemented with a Finite State Machine (FSM) for the RideSharing application.

## Overview

The Trip Lifecycle system manages the complete journey of a ride from request to completion, using a strict state machine to ensure data integrity and proper business logic flow.

## FSM States and Transitions

### States

- **REQUESTED** → Trip has been requested by rider
- **ACCEPTED** → Driver has accepted the trip
- **STARTED** → Trip has started (rider is onboard)
- **COMPLETED** → Trip has been completed successfully
- **CANCELLED** → Trip has been cancelled by either party

### Valid Transitions

```
REQUESTED → ACCEPTED
REQUESTED → CANCELLED
ACCEPTED → STARTED
ACCEPTED → CANCELLED
STARTED → COMPLETED
```

### Invalid Transitions

- Any other transition is rejected with an `IllegalStateException`
- Terminal states (COMPLETED, CANCELLED) cannot transition to any other state

## API Endpoints

### Trip Management

#### Create Trip Request

```
POST /api/trip/request
Content-Type: application/json

{
  "riderId": 1,
  "pickupLocation": {
    "latitude": 28.61,
    "longitude": 77.23
  },
  "dropoffLocation": {
    "latitude": 28.70,
    "longitude": 77.30
  },
  "matchingStrategy": "nearest" // Optional: "nearest", "least-busy", "high-rating"
}
```

#### Accept Trip (Driver)

```
PATCH /api/trip/{id}/accept?driverId={driverId}
```

#### Start Trip (Driver)

```
PATCH /api/trip/{id}/start?driverId={driverId}
```

#### Complete Trip (Driver)

```
PATCH /api/trip/{id}/end?driverId={driverId}
```

#### Cancel Trip

```
PATCH /api/trip/{id}/cancel?reason={reason}&cancelledBy={RIDER|DRIVER}&userId={userId}
```

#### Get Trip Details

```
GET /api/trip/{id}
```

### Trip Queries

#### Get Active Trips for Rider

```
GET /api/trip/rider/{riderId}/active
```

#### Get Active Trips for Driver

```
GET /api/trip/driver/{driverId}/active
```

#### Get Trip History for Rider

```
GET /api/trip/rider/{riderId}/history
```

#### Get Trip History for Driver

```
GET /api/trip/driver/{driverId}/history
```

## Response Format

All endpoints return a consistent response format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "trip": {
    "id": 1,
    "state": "REQUESTED",
    "riderId": 1,
    "riderName": "Latika",
    "driverId": 2,
    "driverName": "Rajesh",
    "pickupLocation": {
      "latitude": 28.61,
      "longitude": 77.23
    },
    "dropoffLocation": {
      "latitude": 28.7,
      "longitude": 77.3
    },
    "requestedAt": "2024-01-15T10:30:00",
    "acceptedAt": null,
    "startedAt": null,
    "completedAt": null,
    "cancelledAt": null,
    "fareAmount": null,
    "distanceKm": 12.5,
    "estimatedDurationMinutes": 25,
    "cancellationReason": null,
    "cancelledBy": null
  }
}
```

## Business Logic

### Trip Creation

1. Validates rider exists and has no active trips
2. Calculates distance between pickup and dropoff locations
3. Estimates trip duration (2 minutes per km)
4. Uses matching strategy to find and assign driver
5. Creates trip in REQUESTED state

### Driver Assignment

- Integrates with existing matching system
- Supports three strategies: nearest, least-busy, high-rating
- Searches for drivers within 10km radius of pickup location
- Automatically assigns best match based on strategy

### State Validation

- Strict validation prevents invalid state transitions
- Driver availability is checked before assignment
- Active trip limits are enforced (1 per rider/driver)

### Fare Calculation

- Base fare: ₹50
- Distance rate: ₹15 per kilometer
- Calculated automatically when trip is completed

## Database Schema

### Trip Entity

```sql
CREATE TABLE trips (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    state VARCHAR(20) NOT NULL,
    rider_id BIGINT NOT NULL,
    driver_id BIGINT,
    pickup_latitude DOUBLE,
    pickup_longitude DOUBLE,
    dropoff_latitude DOUBLE,
    dropoff_longitude DOUBLE,
    requested_at TIMESTAMP NOT NULL,
    accepted_at TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    fare_amount DOUBLE,
    distance_km DOUBLE,
    estimated_duration_minutes INT,
    cancellation_reason VARCHAR(255),
    cancelled_by VARCHAR(10),
    FOREIGN KEY (rider_id) REFERENCES rider_profiles(id),
    FOREIGN KEY (driver_id) REFERENCES driver_profiles(id)
);
```

## Error Handling

### Common Error Scenarios

1. **Invalid State Transition**: Returns 400 Bad Request
2. **Driver Not Available**: Returns 400 Bad Request
3. **Trip Not Found**: Returns 404 Not Found
4. **Unauthorized Action**: Returns 400 Bad Request
5. **No Available Drivers**: Returns 400 Bad Request

### Error Response Format

```json
{
  "success": false,
  "message": "Trip must be in REQUESTED state to be accepted. Current state: STARTED"
}
```

## Integration with Matching System

The Trip system seamlessly integrates with the existing matching strategies:

- **NearestDriverMatcher**: Finds closest driver to pickup location
- **LeastBusyDriverMatcher**: Finds driver with fewest active rides
- **HighRatingDriverMatcher**: Finds highest-rated available driver

## Usage Examples

### Complete Trip Lifecycle

```java
// 1. Create trip request
TripRequestDTO request = new TripRequestDTO();
request.setRiderId(1L);
request.setPickupLocation(new LocationDTO(28.61, 77.23));
request.setDropoffLocation(new LocationDTO(28.70, 77.30));
request.setMatchingStrategy("nearest");

TripResponseDTO trip = tripService.createTripRequest(request);
// State: REQUESTED

// 2. Driver accepts
trip = tripService.acceptTrip(trip.getId(), 2L);
// State: ACCEPTED

// 3. Start trip
trip = tripService.startTrip(trip.getId(), 2L);
// State: STARTED

// 4. Complete trip
trip = tripService.completeTrip(trip.getId(), 2L);
// State: COMPLETED, fare calculated
```

### Cancellation Example

```java
// Cancel from REQUESTED state
tripService.cancelTrip(tripId, "Rider changed mind", CancelledBy.RIDER, riderId);

// Cancel from ACCEPTED state
tripService.cancelTrip(tripId, "Driver unavailable", CancelledBy.DRIVER, driverId);
```

## Testing

Run the example class to see the FSM in action:

```bash
./gradlew run
```

The `TripLifecycleExample` class demonstrates:

- Complete trip lifecycle
- State transitions
- Invalid transition handling
- Cancellation scenarios

## Future Enhancements

1. **Real-time Updates**: WebSocket integration for live trip status
2. **Advanced Fare Calculation**: Time-based, surge pricing, tolls
3. **Trip Tracking**: Real-time location updates during ride
4. **Rating System**: Post-trip rating and feedback
5. **Analytics**: Trip completion rates, average fares, etc.
6. **Notifications**: Push notifications for state changes

## Dependencies

- Spring Boot 3.x
- Spring Data JPA
- Jakarta Persistence API
- Spring Web
- Existing Matching System

## Notes

- All timestamps are stored in UTC
- Distance calculations use Haversine formula
- Driver availability is automatically managed
- State transitions are atomic and validated
- Trip history is preserved for analytics
