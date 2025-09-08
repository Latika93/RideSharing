package com.RideSharing.RideSharing.service;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.Location;
import com.RideSharing.RideSharing.entity.RiderProfile;
import com.RideSharing.RideSharing.repository.DriverProfileRepository;
import com.RideSharing.RideSharing.repository.RiderProfileRepository;
import com.RideSharing.RideSharing.strategy.DriverMatchingStrategy;
import com.RideSharing.RideSharing.strategy.RideMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling driver-rider matching operations
 */
@Service
public class MatchingService {
    
    @Autowired
    private DriverProfileRepository driverProfileRepository;
    
    @Autowired
    private RiderProfileRepository riderProfileRepository;
    
    @Autowired
    private RideMatcher rideMatcher;
    
    /**
     * Update driver's current location
     * @param driverId The ID of the driver
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @return Updated driver profile or null if not found
     */
    public DriverProfile updateDriverLocation(Long driverId, Double latitude, Double longitude) {
        Optional<DriverProfile> driverOpt = driverProfileRepository.findById(driverId);
        if (driverOpt.isPresent()) {
            DriverProfile driver = driverOpt.get();
            Location location = new Location(latitude, longitude);
            driver.setCurrentLocation(location);
            return driverProfileRepository.save(driver);
        }
        return null;
    }
    
    /**
     * Update rider's current location
     * @param riderId The ID of the rider
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @return Updated rider profile or null if not found
     */
    public RiderProfile updateRiderLocation(Long riderId, Double latitude, Double longitude) {
        Optional<RiderProfile> riderOpt = riderProfileRepository.findById(riderId);
        if (riderOpt.isPresent()) {
            RiderProfile rider = riderOpt.get();
            Location location = new Location(latitude, longitude);
            rider.setCurrentLocation(location);
            return riderProfileRepository.save(rider);
        }
        return null;
    }
    
    /**
     * Find nearby available drivers within a specified radius
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param radiusKm The radius in kilometers (optional, defaults to 10km)
     * @return List of nearby available drivers
     */
    public List<DriverProfile> findNearbyDrivers(Double latitude, Double longitude, Double radiusKm) {
        final Double finalRadiusKm = radiusKm != null ? radiusKm : 10.0; // Default 10km radius
        
        Location searchLocation = new Location(latitude, longitude);
        List<DriverProfile> availableDrivers = driverProfileRepository.findByIsAvailableTrue();
        
        return availableDrivers.stream()
                .filter(driver -> driver.getCurrentLocation() != null)
                .filter(driver -> searchLocation.calculateDistance(driver.getCurrentLocation()) <= finalRadiusKm)
                .sorted((d1, d2) -> Double.compare(
                    searchLocation.calculateDistance(d1.getCurrentLocation()),
                    searchLocation.calculateDistance(d2.getCurrentLocation())
                ))
                .toList();
    }
    
    /**
     * Find nearby available drivers with default radius
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @return List of nearby available drivers
     */
    public List<DriverProfile> findNearbyDrivers(Double latitude, Double longitude) {
        return findNearbyDrivers(latitude, longitude, 10.0);
    }
    
    /**
     * Match a driver for a rider using the specified strategy
     * @param riderId The ID of the rider
     * @param strategy The matching strategy to use
     * @return Matched driver or null if no suitable driver found
     */
    public DriverProfile matchDriverForRider(Long riderId, DriverMatchingStrategy strategy) {
        Optional<RiderProfile> riderOpt = riderProfileRepository.findById(riderId);
        if (riderOpt.isEmpty()) {
            return null;
        }
        
        RiderProfile rider = riderOpt.get();
        List<DriverProfile> availableDrivers = driverProfileRepository.findByIsAvailableTrue();
        
        rideMatcher.setStrategy(strategy);
        return rideMatcher.findDriver(rider, availableDrivers);
    }
    
    /**
     * Match a driver for a rider using the nearest driver strategy
     * @param riderId The ID of the rider
     * @return Matched driver or null if no suitable driver found
     */
    public DriverProfile matchNearestDriver(Long riderId) {
        return matchDriverForRider(riderId, rideMatcher.getStrategy());
    }
}
