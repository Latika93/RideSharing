package com.RideSharing.RideSharing.controller;

import com.RideSharing.RideSharing.entity.LocationUpdate;
import com.RideSharing.RideSharing.entity.RiderProfile;
import com.RideSharing.RideSharing.entity.User;
import com.RideSharing.RideSharing.repository.UserRepository;
import com.RideSharing.RideSharing.service.LocationTrackingService;
import com.RideSharing.RideSharing.service.RiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rider")

public class RiderController {

    @Autowired
    private RiderService riderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationTrackingService locationTrackingService;

    /**
     * GET /rider/{id} - Fetch rider profile (default payment method, preferences)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRiderProfile(@PathVariable Long id) {
        
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            // Verify the rider is accessing their own profile
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null || !currentUser.getUserId().equals(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized");
                response.put("message", "You can only access your own profile");
                return ResponseEntity.status(403).body(response);
            }

            RiderProfile riderProfile = riderService.getRiderProfile(id);
            if (riderProfile == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Not Found");
                response.put("message", "Rider profile not found");
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("riderId", id);
            response.put("username", currentUser.getUsername());
            response.put("email", currentUser.getEmail());
            response.put("phoneNumber", riderProfile.getPhoneNumber());
            response.put("paymentMethod", riderProfile.getPaymentMethod());
            response.put("preferences", riderProfile.getPreferences());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * PATCH /rider/{id} - Update profile (preferred vehicle, payment method, etc.)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRiderProfile(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updateRequest) {
        
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            // Verify the rider is updating their own profile
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null || !currentUser.getUserId().equals(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized");
                response.put("message", "You can only update your own profile");
                return ResponseEntity.status(403).body(response);
            }

            RiderProfile updatedProfile = riderService.updateRiderProfile(id, updateRequest);
            if (updatedProfile == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Not Found");
                response.put("message", "Rider profile not found");
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rider profile updated successfully");
            response.put("riderId", id);
            response.put("phoneNumber", updatedProfile.getPhoneNumber());
            response.put("paymentMethod", updatedProfile.getPaymentMethod());
            response.put("preferences", updatedProfile.getPreferences());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /rider/{id}/driver/{driverId}/location - Get driver's current location for a rider
     */
    @GetMapping("/{id}/driver/{driverId}/location")
    public ResponseEntity<Map<String, Object>> getDriverLocationForRider(
            @PathVariable Long id,
            @PathVariable Long driverId) {
        
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            // Verify the rider is accessing their own data
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null || !currentUser.getUserId().equals(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized");
                response.put("message", "You can only access your own data");
                return ResponseEntity.status(403).body(response);
            }

            LocationUpdate driverLocation = locationTrackingService.getLatestDriverLocation(driverId.toString());
            
            if (driverLocation == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Not Found");
                response.put("message", "Driver location not available");
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("driverId", driverId);
            response.put("latitude", driverLocation.getLatitude());
            response.put("longitude", driverLocation.getLongitude());
            response.put("timestamp", driverLocation.getTimestamp());
            response.put("speed", driverLocation.getSpeed());
            response.put("heading", driverLocation.getHeading());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /rider/{id}/drivers/nearby - Get nearby drivers for a rider
     */
    @GetMapping("/{id}/drivers/nearby")
    public ResponseEntity<Map<String, Object>> getNearbyDrivers(
            @PathVariable Long id,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radiusKm) {
        
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            // Verify the rider is accessing their own data
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null || !currentUser.getUserId().equals(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized");
                response.put("message", "You can only access your own data");
                return ResponseEntity.status(403).body(response);
            }

            java.util.List<LocationUpdate> nearbyDrivers = 
                locationTrackingService.getDriversNearLocation(latitude, longitude, radiusKm);
            
            Map<String, Object> response = new HashMap<>();
            response.put("riderId", id);
            response.put("nearbyDrivers", nearbyDrivers);
            response.put("count", nearbyDrivers.size());
            response.put("searchLocation", Map.of("latitude", latitude, "longitude", longitude));
            response.put("radiusKm", radiusKm);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
