package com.RideSharing.RideSharing.strategy;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.RiderProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Comparator;

/**
 * Strategy to match the driver with the least number of active rides
 */
@Component
public class LeastBusyDriverMatcher implements DriverMatchingStrategy {
    
    @Override
    public DriverProfile matchDriver(RiderProfile rider, List<DriverProfile> availableDrivers) {
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return null;
        }
        
        return availableDrivers.stream()
                .min(Comparator.comparing(driver -> 
                    driver.getActiveRides() != null ? driver.getActiveRides() : 0))
                .orElse(null);
    }
}
