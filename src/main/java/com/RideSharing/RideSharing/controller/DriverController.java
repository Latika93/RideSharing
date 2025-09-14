package com.RideSharing.RideSharing.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.LocationUpdate;
import com.RideSharing.RideSharing.entity.LocationUpdateDTO;
import com.RideSharing.RideSharing.entity.User;
import com.RideSharing.RideSharing.repository.DriverProfileRepository;
import com.RideSharing.RideSharing.repository.UserRepository;
import com.RideSharing.RideSharing.service.DriverService;
import com.RideSharing.RideSharing.service.LocationTrackingService;

@RestController
@RequestMapping("/driver")
@CrossOrigin(origins = "http://localhost:5173")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverProfileRepository driverProfileRepository;

    @Autowired
    private LocationTrackingService locationTrackingService;

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

    /**
     * POST /driver/{id}/location - Update driver's current location
     */
    @PostMapping("/{id}/location")
    public ResponseEntity<Map<String, Object>> updateDriverLocation(
            @PathVariable Long id,
            @RequestBody LocationUpdateDTO locationUpdate) {

        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            // Verify the driver is updating their own location
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null || !currentUser.getUserId().equals(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized");
                response.put("message", "You can only update your own location");
                return ResponseEntity.status(403).body(response);
            }

            // Validate location data
            if (locationUpdate.getLatitude() == null || locationUpdate.getLongitude() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Bad Request");
                response.put("message", "Latitude and longitude are required");
                return ResponseEntity.badRequest().body(response);
            }

            // Set driver ID and timestamp
            locationUpdate.setDriverId(id.toString());
            locationUpdate.setTimestamp(java.time.LocalDateTime.now());

            // Store the location update
            locationTrackingService.storeLocationUpdate(locationUpdate.toLocationUpdate());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Location updated successfully");
            response.put("driverId", id);
            response.put("latitude", locationUpdate.getLatitude());
            response.put("longitude", locationUpdate.getLongitude());
            response.put("timestamp", locationUpdate.getTimestamp());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /driver/{id}/location - Get driver's current location
     */
    @GetMapping("/{id}/location")
    public ResponseEntity<Map<String, Object>> getDriverLocation(@PathVariable Long id) {

        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            // Verify the driver is accessing their own location
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null || !currentUser.getUserId().equals(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized");
                response.put("message", "You can only access your own location");
                return ResponseEntity.status(403).body(response);
            }

            LocationUpdate latestLocation = locationTrackingService.getLatestDriverLocation(id.toString());

            if (latestLocation == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Not Found");
                response.put("message", "No location data found for driver");
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("driverId", id);
            response.put("latitude", latestLocation.getLatitude());
            response.put("longitude", latestLocation.getLongitude());
            response.put("timestamp", latestLocation.getTimestamp());
            response.put("speed", latestLocation.getSpeed());
            response.put("heading", latestLocation.getHeading());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /driver/{id}/location/history - Get driver's location history
     */
    @GetMapping("/{id}/location/history")
    public ResponseEntity<Map<String, Object>> getDriverLocationHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            // Verify the driver is accessing their own location history
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null || !currentUser.getUserId().equals(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized");
                response.put("message", "You can only access your own location history");
                return ResponseEntity.status(403).body(response);
            }

            java.util.List<LocationUpdate> locationHistory = locationTrackingService
                    .getDriverLocationHistory(id.toString(), limit);

            Map<String, Object> response = new HashMap<>();
            response.put("driverId", id);
            response.put("locationHistory", locationHistory);
            response.put("count", locationHistory.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
