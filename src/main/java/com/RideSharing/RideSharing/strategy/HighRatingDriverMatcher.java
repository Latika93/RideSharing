package com.RideSharing.RideSharing.strategy;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.RiderProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Comparator;

/**
 * Strategy to match the driver with the highest rating
 */
@Component
public class HighRatingDriverMatcher implements DriverMatchingStrategy {
    
    @Override
    public DriverProfile matchDriver(RiderProfile rider, List<DriverProfile> availableDrivers) {
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return null;
        }
        
        return availableDrivers.stream()
                .max(Comparator.comparing(driver -> 
                    driver.getRating() != null ? driver.getRating() : 0.0))
                .orElse(null);
    }
}
