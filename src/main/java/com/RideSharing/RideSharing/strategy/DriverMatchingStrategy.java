package com.RideSharing.RideSharing.strategy;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.RiderProfile;
import java.util.List;

/**
 * Strategy interface for driver matching algorithms
 */
public interface DriverMatchingStrategy {
    
    /**
     * Match a driver for a rider based on the specific strategy
     * @param rider The rider requesting a ride
     * @param availableDrivers List of available drivers
     * @return The matched driver, or null if no suitable driver found
     */
    DriverProfile matchDriver(RiderProfile rider, List<DriverProfile> availableDrivers);
}
