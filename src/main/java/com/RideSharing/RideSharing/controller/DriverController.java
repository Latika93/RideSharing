package com.RideSharing.RideSharing.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.User;
import com.RideSharing.RideSharing.repository.DriverProfileRepository;
import com.RideSharing.RideSharing.repository.UserRepository;
import com.RideSharing.RideSharing.service.DriverService;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverProfileRepository driverProfileRepository;

    /**
     * PATCH /driver/{id}/status - Driver marks themselves online/offline
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateDriverStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> statusRequest) {
        
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            // Verify the driver is updating their own status
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null || !currentUser.getUserId().equals(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized");
                response.put("message", "You can only update your own status");
                return ResponseEntity.status(403).body(response);
            }

            Boolean isAvailable = statusRequest.get("isAvailable");
            if (isAvailable == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Bad Request");
                response.put("message", "isAvailable field is required");
                return ResponseEntity.badRequest().body(response);
            }

            DriverProfile updatedProfile = driverService.updateDriverAvailability(id, isAvailable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Driver status updated successfully");
            response.put("driverId", id);
            response.put("isAvailable", updatedProfile.isAvailable());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /driver/{id} - Fetch driver profile and current availability
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDriverProfile(@PathVariable Long id) {
        
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            // Verify the driver is accessing their own profile
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null || !currentUser.getUserId().equals(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized");
                response.put("message", "You can only access your own profile");
                return ResponseEntity.status(403).body(response);
            }

            DriverProfile driverProfile = driverService.getDriverProfile(id);
            if (driverProfile == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Not Found");
                response.put("message", "Driver profile not found");
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("driverId", id);
            response.put("username", currentUser.getUsername());
            response.put("email", currentUser.getEmail());
            response.put("licenseNumber", driverProfile.getLicenseNumber());
            response.put("vehicleMake", driverProfile.getVehicleMake());
            response.put("vehicleModel", driverProfile.getVehicleModel());
            response.put("vehicleYear", driverProfile.getVehicleYear());
            response.put("vehicleColor", driverProfile.getVehicleColor());
            response.put("vehiclePlateNumber", driverProfile.getVehiclePlateNumber());
            response.put("isAvailable", driverProfile.isAvailable());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
