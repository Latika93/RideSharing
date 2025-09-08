package com.RideSharing.RideSharing.service;

import com.RideSharing.RideSharing.entity.*;
import com.RideSharing.RideSharing.repository.DriverProfileRepository;
import com.RideSharing.RideSharing.repository.RiderProfileRepository;
import com.RideSharing.RideSharing.repository.TripRepository;
import com.RideSharing.RideSharing.strategy.DriverMatchingStrategy;
import com.RideSharing.RideSharing.strategy.HighRatingDriverMatcher;
import com.RideSharing.RideSharing.strategy.LeastBusyDriverMatcher;
import com.RideSharing.RideSharing.strategy.NearestDriverMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling trip lifecycle operations with FSM validation
 */
@Service
@Transactional
public class TripService {
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private RiderProfileRepository riderProfileRepository;
    
    @Autowired
    private DriverProfileRepository driverProfileRepository;
    
    @Autowired
    private MatchingService matchingService;
    
    @Autowired
    private NearestDriverMatcher nearestDriverMatcher;
    
    @Autowired
    private LeastBusyDriverMatcher leastBusyDriverMatcher;
    
    @Autowired
    private HighRatingDriverMatcher highRatingDriverMatcher;
    
    /**
     * Create a new trip request
     * POST /trip/request
     */
    public TripResponseDTO createTripRequest(TripRequestDTO tripRequestDTO) {
        // Validate rider exists
        RiderProfile rider = riderProfileRepository.findById(tripRequestDTO.getRiderId())
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with ID: " + tripRequestDTO.getRiderId()));
        
        // Check if rider has any active trips
        List<Trip> activeTrips = tripRepository.findActiveTripsByRider(rider);
        if (!activeTrips.isEmpty()) {
            throw new IllegalStateException("Rider already has an active trip");
        }
        
        // Create trip entity
        Location pickupLocation = tripRequestDTO.getPickupLocation().toLocation();
        Location dropoffLocation = tripRequestDTO.getDropoffLocation().toLocation();
        
        Trip trip = new Trip(rider, pickupLocation, dropoffLocation);
        
        // Calculate distance and estimated duration
        double distance = pickupLocation.calculateDistance(dropoffLocation);
        trip.setDistanceKm(distance);
        trip.setEstimatedDurationMinutes((int) (distance * 2)); // Rough estimate: 2 minutes per km
        
        // Find and assign driver using matching strategy
        DriverProfile matchedDriver = findAndAssignDriver(trip, tripRequestDTO.getMatchingStrategy());
        
        // Save trip
        Trip savedTrip = tripRepository.save(trip);
        
        return new TripResponseDTO(savedTrip);
    }
    
    /**
     * Driver accepts a trip
     * PATCH /trip/{id}/accept
     */
    public TripResponseDTO acceptTrip(Long tripId, Long driverId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));
        
        DriverProfile driver = driverProfileRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with ID: " + driverId));
        
        // Validate trip state
        if (trip.getState() != TripState.REQUESTED) {
            throw new IllegalStateException("Trip must be in REQUESTED state to be accepted. Current state: " + trip.getState());
        }
        
        // Validate driver is available
        if (!driver.isAvailable()) {
            throw new IllegalStateException("Driver is not available");
        }
        
        // Check if driver has any active trips
        List<Trip> activeTrips = tripRepository.findActiveTripsByDriver(driver);
        if (!activeTrips.isEmpty()) {
            throw new IllegalStateException("Driver already has an active trip");
        }
        
        // Assign driver and transition to ACCEPTED
        trip.assignDriver(driver);
        
        // Update driver availability
        driver.setAvailable(false);
        driverProfileRepository.save(driver);
        
        Trip savedTrip = tripRepository.save(trip);
        
        return new TripResponseDTO(savedTrip);
    }
    
    /**
     * Start a trip (rider is onboard)
     * PATCH /trip/{id}/start
     */
    public TripResponseDTO startTrip(Long tripId, Long driverId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));
        
        // Validate trip state
        if (trip.getState() != TripState.ACCEPTED) {
            throw new IllegalStateException("Trip must be in ACCEPTED state to be started. Current state: " + trip.getState());
        }
        
        // Validate driver
        if (trip.getDriver() == null || !trip.getDriver().getId().equals(driverId)) {
            throw new IllegalArgumentException("Driver is not assigned to this trip or driver ID mismatch");
        }
        
        // Start trip
        trip.startTrip();
        
        Trip savedTrip = tripRepository.save(trip);
        
        return new TripResponseDTO(savedTrip);
    }
    
    /**
     * Complete a trip
     * PATCH /trip/{id}/end
     */
    public TripResponseDTO completeTrip(Long tripId, Long driverId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));
        
        // Validate trip state
        if (trip.getState() != TripState.STARTED) {
            throw new IllegalStateException("Trip must be in STARTED state to be completed. Current state: " + trip.getState());
        }
        
        // Validate driver
        if (trip.getDriver() == null || !trip.getDriver().getId().equals(driverId)) {
            throw new IllegalArgumentException("Driver is not assigned to this trip or driver ID mismatch");
        }
        
        // Complete trip
        trip.completeTrip();
        
        // Update driver availability and active rides count
        DriverProfile driver = trip.getDriver();
        driver.setAvailable(true);
        driver.setActiveRides(driver.getActiveRides() != null ? driver.getActiveRides() - 1 : 0);
        driverProfileRepository.save(driver);
        
        Trip savedTrip = tripRepository.save(trip);
        
        return new TripResponseDTO(savedTrip);
    }
    
    /**
     * Cancel a trip
     * PATCH /trip/{id}/cancel
     */
    public TripResponseDTO cancelTrip(Long tripId, String reason, CancelledBy cancelledBy, Long userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));
        
        // Validate trip state
        if (trip.getState() != TripState.REQUESTED && trip.getState() != TripState.ACCEPTED) {
            throw new IllegalStateException("Trip can only be cancelled from REQUESTED or ACCEPTED state. Current state: " + trip.getState());
        }
        
        // Validate user authorization
        boolean isAuthorized = false;
        if (cancelledBy == CancelledBy.RIDER) {
            isAuthorized = trip.getRider() != null && trip.getRider().getId().equals(userId);
        } else if (cancelledBy == CancelledBy.DRIVER) {
            isAuthorized = trip.getDriver() != null && trip.getDriver().getId().equals(userId);
        }
        
        if (!isAuthorized) {
            throw new IllegalArgumentException("User is not authorized to cancel this trip");
        }
        
        // Cancel trip
        trip.cancelTrip(reason, cancelledBy);
        
        // If driver was assigned, make them available again
        if (trip.getDriver() != null) {
            DriverProfile driver = trip.getDriver();
            driver.setAvailable(true);
            driverProfileRepository.save(driver);
        }
        
        Trip savedTrip = tripRepository.save(trip);
        
        return new TripResponseDTO(savedTrip);
    }
    
    /**
     * Get trip details
     * GET /trip/{id}
     */
    public TripResponseDTO getTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));
        
        return new TripResponseDTO(trip);
    }
    
    /**
     * Get active trips for a rider
     */
    public List<TripResponseDTO> getActiveTripsByRider(Long riderId) {
        RiderProfile rider = riderProfileRepository.findById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with ID: " + riderId));
        
        List<Trip> activeTrips = tripRepository.findActiveTripsByRider(rider);
        
        return activeTrips.stream()
                .map(TripResponseDTO::new)
                .toList();
    }
    
    /**
     * Get active trips for a driver
     */
    public List<TripResponseDTO> getActiveTripsByDriver(Long driverId) {
        DriverProfile driver = driverProfileRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with ID: " + driverId));
        
        List<Trip> activeTrips = tripRepository.findActiveTripsByDriver(driver);
        
        return activeTrips.stream()
                .map(TripResponseDTO::new)
                .toList();
    }
    
    /**
     * Get trip history for a rider
     */
    public List<TripResponseDTO> getTripHistoryByRider(Long riderId) {
        RiderProfile rider = riderProfileRepository.findById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with ID: " + riderId));
        
        List<Trip> trips = tripRepository.findByRiderOrderByRequestedAtDesc(rider);
        
        return trips.stream()
                .map(TripResponseDTO::new)
                .toList();
    }
    
    /**
     * Get trip history for a driver
     */
    public List<TripResponseDTO> getTripHistoryByDriver(Long driverId) {
        DriverProfile driver = driverProfileRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with ID: " + driverId));
        
        List<Trip> trips = tripRepository.findByDriverOrderByRequestedAtDesc(driver);
        
        return trips.stream()
                .map(TripResponseDTO::new)
                .toList();
    }
    
    /**
     * Find and assign driver using matching strategy
     */
    private DriverProfile findAndAssignDriver(Trip trip, String strategyName) {
        DriverMatchingStrategy strategy;
        
        switch (strategyName != null ? strategyName.toLowerCase() : "nearest") {
            case "least-busy" -> strategy = leastBusyDriverMatcher;
            case "high-rating" -> strategy = highRatingDriverMatcher;
            default -> strategy = nearestDriverMatcher;
        }
        
        // Find available drivers near pickup location
        List<DriverProfile> nearbyDrivers = matchingService.findNearbyDrivers(
                trip.getPickupLocation().getLatitude(),
                trip.getPickupLocation().getLongitude(),
                10.0 // 10km radius
        );
        
        if (nearbyDrivers.isEmpty()) {
            throw new IllegalStateException("No available drivers found near the pickup location");
        }
        
        // Use strategy to match driver
        DriverProfile matchedDriver = strategy.matchDriver(trip.getRider(), nearbyDrivers);
        
        if (matchedDriver == null) {
            throw new IllegalStateException("No suitable driver found using the specified strategy");
        }
        
        // Assign driver to trip
        trip.setDriver(matchedDriver);
        
        return matchedDriver;
    }
}
