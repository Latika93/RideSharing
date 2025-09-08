package com.RideSharing.RideSharing.controller;

import com.RideSharing.RideSharing.entity.RiderProfile;
import com.RideSharing.RideSharing.entity.User;
import com.RideSharing.RideSharing.repository.UserRepository;
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
}
