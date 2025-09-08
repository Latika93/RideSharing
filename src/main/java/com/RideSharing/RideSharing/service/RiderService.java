package com.RideSharing.RideSharing.service;

import com.RideSharing.RideSharing.entity.RiderProfile;
import com.RideSharing.RideSharing.entity.User;
import com.RideSharing.RideSharing.repository.RiderProfileRepository;
import com.RideSharing.RideSharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RiderService {

    @Autowired
    private RiderProfileRepository riderProfileRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get rider profile by user ID
     */
    public RiderProfile getRiderProfile(Long riderId) {
        User user = userRepository.findById(riderId).orElse(null);
        if (user == null) {
            return null;
        }

        return riderProfileRepository.findByUser(user);
    }

    /**
     * Update rider profile with provided fields
     */
    public RiderProfile updateRiderProfile(Long riderId, Map<String, Object> updateRequest) {
        User user = userRepository.findById(riderId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + riderId);
        }

        RiderProfile riderProfile = riderProfileRepository.findByUser(user);
        if (riderProfile == null) {
            throw new RuntimeException("Rider profile not found for user ID: " + riderId);
        }

        // Update fields if provided in the request
        if (updateRequest.containsKey("phoneNumber")) {
            riderProfile.setPhoneNumber((String) updateRequest.get("phoneNumber"));
        }

        if (updateRequest.containsKey("paymentMethod")) {
            riderProfile.setPaymentMethod((String) updateRequest.get("paymentMethod"));
        }

        if (updateRequest.containsKey("preferences")) {
            riderProfile.setPreferences((String) updateRequest.get("preferences"));
        }

        return riderProfileRepository.save(riderProfile);
    }

    /**
     * Check if rider exists and has a profile
     */
    public boolean riderExists(Long riderId) {
        User user = userRepository.findById(riderId).orElse(null);
        if (user == null) {
            return false;
        }

        return riderProfileRepository.findByUser(user) != null;
    }
}
