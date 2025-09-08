package com.RideSharing.RideSharing.strategy;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.RiderProfile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Context class for the Strategy pattern
 * Manages the driver matching strategy and provides a unified interface
 */
@Component
public class RideMatcher {
    
    private DriverMatchingStrategy strategy;
    
    /**
     * Set the matching strategy at runtime
     * @param strategy The strategy to use for matching
     */
    public void setStrategy(DriverMatchingStrategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Find a driver for the given rider using the current strategy
     * @param rider The rider requesting a ride
     * @param availableDrivers List of available drivers
     * @return The matched driver, or null if no suitable driver found
     * @throws IllegalStateException if no strategy is set
     */
    public DriverProfile findDriver(RiderProfile rider, List<DriverProfile> availableDrivers) {
        if (strategy == null) {
            throw new IllegalStateException("Driver matching strategy not set!");
        }
        return strategy.matchDriver(rider, availableDrivers);
    }
    
    /**
     * Get the current strategy
     * @return The current matching strategy
     */
    public DriverMatchingStrategy getStrategy() {
        return strategy;
    }
}
