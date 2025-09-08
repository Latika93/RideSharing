package com.RideSharing.RideSharing.strategy;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.RiderProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Comparator;

/**
 * Strategy to match the nearest available driver to the rider
 */
@Component
public class NearestDriverMatcher implements DriverMatchingStrategy {
    
    @Override
    public DriverProfile matchDriver(RiderProfile rider, List<DriverProfile> availableDrivers) {
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return null;
        }
        
        if (rider.getCurrentLocation() == null) {
            // If rider location is not available, return first available driver
            return availableDrivers.get(0);
        }
        
        return availableDrivers.stream()
                .filter(driver -> driver.getCurrentLocation() != null)
                .min(Comparator.comparing(driver -> 
                    rider.getCurrentLocation().calculateDistance(driver.getCurrentLocation())))
                .orElse(null);
    }
}
