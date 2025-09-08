package com.RideSharing.RideSharing.controller;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.RiderProfile;
import com.RideSharing.RideSharing.service.MatchingService;
import com.RideSharing.RideSharing.strategy.HighRatingDriverMatcher;
import com.RideSharing.RideSharing.strategy.LeastBusyDriverMatcher;
import com.RideSharing.RideSharing.strategy.NearestDriverMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling driver-rider matching operations
 */
@RestController
@RequestMapping("/api/matching")
@CrossOrigin(origins = "*")
public class MatchingController {
    
    @Autowired
    private MatchingService matchingService;
    
    @Autowired
    private NearestDriverMatcher nearestDriverMatcher;
    
    @Autowired
    private LeastBusyDriverMatcher leastBusyDriverMatcher;
    
    @Autowired
    private HighRatingDriverMatcher highRatingDriverMatcher;
    
    /**
     * Update driver's current location
     * PATCH /driver/{id}/location
     */
    @PatchMapping("/driver/{id}/location")
    public ResponseEntity<Map<String, Object>> updateDriverLocation(
            @PathVariable Long id,
            @RequestParam Double lat,
            @RequestParam Double lng) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            DriverProfile updatedDriver = matchingService.updateDriverLocation(id, lat, lng);
            if (updatedDriver != null) {
                response.put("success", true);
                response.put("message", "Driver location updated successfully");
                response.put("driverId", updatedDriver.getId());
                response.put("location", updatedDriver.getCurrentLocation());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Driver not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating driver location: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Update rider's current location
     * PATCH /rider/{id}/location
     */
    @PatchMapping("/rider/{id}/location")
    public ResponseEntity<Map<String, Object>> updateRiderLocation(
            @PathVariable Long id,
            @RequestParam Double lat,
            @RequestParam Double lng) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            RiderProfile updatedRider = matchingService.updateRiderLocation(id, lat, lng);
            if (updatedRider != null) {
                response.put("success", true);
                response.put("message", "Rider location updated successfully");
                response.put("riderId", updatedRider.getId());
                response.put("location", updatedRider.getCurrentLocation());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Rider not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating rider location: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get nearby available drivers
     * GET /drivers/nearby?lat=..&lng=..&radius=..
     */
    @GetMapping("/drivers/nearby")
    public ResponseEntity<Map<String, Object>> getNearbyDrivers(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) Double radius) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<DriverProfile> nearbyDrivers;
            if (radius != null) {
                nearbyDrivers = matchingService.findNearbyDrivers(lat, lng, radius);
            } else {
                nearbyDrivers = matchingService.findNearbyDrivers(lat, lng);
            }
            
            response.put("success", true);
            response.put("message", "Nearby drivers retrieved successfully");
            response.put("count", nearbyDrivers.size());
            response.put("drivers", nearbyDrivers);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving nearby drivers: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Match a driver for a rider using nearest driver strategy
     * POST /match/nearest
     */
    @PostMapping("/match/nearest")
    public ResponseEntity<Map<String, Object>> matchNearestDriver(@RequestParam Long riderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DriverProfile matchedDriver = matchingService.matchDriverForRider(riderId, nearestDriverMatcher);
            if (matchedDriver != null) {
                response.put("success", true);
                response.put("message", "Nearest driver matched successfully");
                response.put("driver", matchedDriver);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No available drivers found");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error matching driver: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Match a driver for a rider using least busy driver strategy
     * POST /match/least-busy
     */
    @PostMapping("/match/least-busy")
    public ResponseEntity<Map<String, Object>> matchLeastBusyDriver(@RequestParam Long riderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DriverProfile matchedDriver = matchingService.matchDriverForRider(riderId, leastBusyDriverMatcher);
            if (matchedDriver != null) {
                response.put("success", true);
                response.put("message", "Least busy driver matched successfully");
                response.put("driver", matchedDriver);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No available drivers found");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error matching driver: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Match a driver for a rider using high rating driver strategy
     * POST /match/high-rating
     */
    @PostMapping("/match/high-rating")
    public ResponseEntity<Map<String, Object>> matchHighRatingDriver(@RequestParam Long riderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DriverProfile matchedDriver = matchingService.matchDriverForRider(riderId, highRatingDriverMatcher);
            if (matchedDriver != null) {
                response.put("success", true);
                response.put("message", "High rating driver matched successfully");
                response.put("driver", matchedDriver);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No available drivers found");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error matching driver: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
