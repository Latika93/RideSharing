package com.RideSharing.RideSharing.service;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.User;
import com.RideSharing.RideSharing.repository.DriverProfileRepository;
import com.RideSharing.RideSharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    @Autowired
    private DriverProfileRepository driverProfileRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Update driver availability status
     */
    public DriverProfile updateDriverAvailability(Long driverId, boolean isAvailable) {
        User user = userRepository.findById(driverId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + driverId);
        }

        DriverProfile driverProfile = driverProfileRepository.findByUser(user);
        if (driverProfile == null) {
            throw new RuntimeException("Driver profile not found for user ID: " + driverId);
        }

        driverProfile.setAvailable(isAvailable);
        return driverProfileRepository.save(driverProfile);
    }

    /**
     * Get driver profile by user ID
     */
    public DriverProfile getDriverProfile(Long driverId) {
        User user = userRepository.findById(driverId).orElse(null);
        if (user == null) {
            return null;
        }

        return driverProfileRepository.findByUser(user);
    }

    /**
     * Check if driver exists and has a profile
     */
    public boolean driverExists(Long driverId) {
        User user = userRepository.findById(driverId).orElse(null);
        if (user == null) {
            return false;
        }

        return driverProfileRepository.findByUser(user) != null;
    }
}
