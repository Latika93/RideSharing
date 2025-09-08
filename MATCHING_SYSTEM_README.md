# Driver Matching System

This document describes the driver matching system implemented using the Strategy pattern for the RideSharing application.

## Overview

The matching system allows riders to find suitable drivers based on different criteria such as proximity, availability, and rating. The system is designed using the Strategy pattern to make it easy to add new matching algorithms in the future.

## Architecture

### Strategy Pattern Components

1. **DriverMatchingStrategy Interface**: Defines the contract for all matching strategies
2. **Concrete Strategies**: Implement specific matching algorithms
3. **RideMatcher Context**: Manages the current strategy and provides a unified interface
4. **MatchingService**: Business logic layer for matching operations
5. **MatchingController**: REST API endpoints for matching operations

### Entities

- **Location**: Embeddable entity for GPS coordinates with distance calculation
- **DriverProfile**: Enhanced with location, rating, and active rides count
- **RiderProfile**: Enhanced with current location

## Available Matching Strategies

### 1. NearestDriverMatcher

- Finds the driver closest to the rider's location
- Uses Haversine formula for accurate distance calculation
- Prioritizes proximity over other factors

### 2. LeastBusyDriverMatcher

- Finds the driver with the fewest active rides
- Helps distribute ride requests evenly among drivers
- Useful for load balancing

### 3. HighRatingDriverMatcher

- Finds the driver with the highest rating
- Prioritizes quality of service
- Ensures riders get experienced drivers

## API Endpoints

### Location Updates

#### Update Driver Location

```
PATCH /api/matching/driver/{id}/location?lat={latitude}&lng={longitude}
```

#### Update Rider Location

```
PATCH /api/matching/rider/{id}/location?lat={latitude}&lng={longitude}
```

### Driver Discovery

#### Find Nearby Drivers

```
GET /api/matching/drivers/nearby?lat={latitude}&lng={longitude}&radius={radiusKm}
```

### Driver Matching

#### Match Nearest Driver

```
POST /api/matching/match/nearest?riderId={riderId}
```

#### Match Least Busy Driver

```
POST /api/matching/match/least-busy?riderId={riderId}
```

#### Match High Rating Driver

```
POST /api/matching/match/high-rating?riderId={riderId}
```

## Usage Examples

### Basic Usage

```java
// Create a ride matcher
RideMatcher matcher = new RideMatcher();

// Set strategy to find nearest driver
matcher.setStrategy(new NearestDriverMatcher());

// Find a driver for a rider
DriverProfile matchedDriver = matcher.findDriver(rider, availableDrivers);
```

### Service Layer Usage

```java
@Autowired
private MatchingService matchingService;

// Update driver location
matchingService.updateDriverLocation(driverId, 28.61, 77.23);

// Find nearby drivers
List<DriverProfile> nearbyDrivers = matchingService.findNearbyDrivers(28.61, 77.23, 5.0);

// Match a driver using specific strategy
DriverProfile driver = matchingService.matchDriverForRider(riderId, new NearestDriverMatcher());
```

## Location and Distance Calculation

The system uses the Haversine formula to calculate distances between GPS coordinates:

```java
Location riderLocation = new Location(28.61, 77.23);
Location driverLocation = new Location(28.62, 77.24);
double distance = riderLocation.calculateDistance(driverLocation); // Returns distance in kilometers
```

## Adding New Matching Strategies

To add a new matching strategy:

1. Implement the `DriverMatchingStrategy` interface:

```java
@Component
public class CustomDriverMatcher implements DriverMatchingStrategy {
    @Override
    public DriverProfile matchDriver(RiderProfile rider, List<DriverProfile> availableDrivers) {
        // Implement your custom matching logic
        return null;
    }
}
```

2. Add the strategy to the controller:

```java
@Autowired
private CustomDriverMatcher customDriverMatcher;

@PostMapping("/match/custom")
public ResponseEntity<Map<String, Object>> matchCustomDriver(@RequestParam Long riderId) {
    // Use the custom strategy
    DriverProfile matchedDriver = matchingService.matchDriverForRider(riderId, customDriverMatcher);
    // Return response
}
```

## Configuration

The system uses Spring Boot's dependency injection. All strategies are automatically registered as Spring components and can be injected where needed.

## Future Enhancements

1. **Composite Strategies**: Combine multiple criteria (e.g., nearest + high rating)
2. **Machine Learning**: Use ML algorithms for optimal matching
3. **Real-time Updates**: WebSocket integration for live location updates
4. **Caching**: Redis integration for improved performance
5. **Analytics**: Track matching success rates and driver performance

## Testing

Run the example class to see the matching system in action:

```bash
./gradlew run
```

The `MatchingExample` class demonstrates all three matching strategies with sample data.

## Dependencies

- Spring Boot 3.x
- Spring Data JPA
- Jakarta Persistence API
- Spring Web

## Notes

- All coordinates are stored as Double values (latitude, longitude)
- Distance calculations return values in kilometers
- The system assumes drivers are available when `isAvailable` is true
- Default search radius is 10km for nearby driver searches
- All strategies return null if no suitable driver is found
