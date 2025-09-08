package com.RideSharing.RideSharing.controller;

import com.RideSharing.RideSharing.entity.*;
import com.RideSharing.RideSharing.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling trip lifecycle operations
 */
@RestController
@RequestMapping("/api/trip")
@CrossOrigin(origins = "*")
public class TripController {
    
    @Autowired
    private TripService tripService;
    
    /**
     * Create a new trip request
     * POST /trip/request
     */
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> createTripRequest(@RequestBody TripRequestDTO tripRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            TripResponseDTO trip = tripService.createTripRequest(tripRequestDTO);
            
            response.put("success", true);
            response.put("message", "Trip request created successfully");
            response.put("trip", trip);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating trip request: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Driver accepts a trip
     * PATCH /trip/{id}/accept
     */
    @PatchMapping("/{id}/accept")
    public ResponseEntity<Map<String, Object>> acceptTrip(
            @PathVariable Long id,
            @RequestParam Long driverId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            TripResponseDTO trip = tripService.acceptTrip(id, driverId);
            
            response.put("success", true);
            response.put("message", "Trip accepted successfully");
            response.put("trip", trip);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error accepting trip: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Start a trip (rider is onboard)
     * PATCH /trip/{id}/start
     */
    @PatchMapping("/{id}/start")
    public ResponseEntity<Map<String, Object>> startTrip(
            @PathVariable Long id,
            @RequestParam Long driverId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            TripResponseDTO trip = tripService.startTrip(id, driverId);
            
            response.put("success", true);
            response.put("message", "Trip started successfully");
            response.put("trip", trip);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error starting trip: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Complete a trip
     * PATCH /trip/{id}/end
     */
    @PatchMapping("/{id}/end")
    public ResponseEntity<Map<String, Object>> completeTrip(
            @PathVariable Long id,
            @RequestParam Long driverId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            TripResponseDTO trip = tripService.completeTrip(id, driverId);
            
            response.put("success", true);
            response.put("message", "Trip completed successfully");
            response.put("trip", trip);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error completing trip: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Cancel a trip
     * PATCH /trip/{id}/cancel
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelTrip(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam CancelledBy cancelledBy,
            @RequestParam Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            TripResponseDTO trip = tripService.cancelTrip(id, reason, cancelledBy, userId);
            
            response.put("success", true);
            response.put("message", "Trip cancelled successfully");
            response.put("trip", trip);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error cancelling trip: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get trip details
     * GET /trip/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTrip(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            TripResponseDTO trip = tripService.getTrip(id);
            
            response.put("success", true);
            response.put("message", "Trip retrieved successfully");
            response.put("trip", trip);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving trip: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get active trips for a rider
     * GET /trip/rider/{riderId}/active
     */
    @GetMapping("/rider/{riderId}/active")
    public ResponseEntity<Map<String, Object>> getActiveTripsByRider(@PathVariable Long riderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<TripResponseDTO> trips = tripService.getActiveTripsByRider(riderId);
            
            response.put("success", true);
            response.put("message", "Active trips retrieved successfully");
            response.put("trips", trips);
            response.put("count", trips.size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving active trips: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get active trips for a driver
     * GET /trip/driver/{driverId}/active
     */
    @GetMapping("/driver/{driverId}/active")
    public ResponseEntity<Map<String, Object>> getActiveTripsByDriver(@PathVariable Long driverId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<TripResponseDTO> trips = tripService.getActiveTripsByDriver(driverId);
            
            response.put("success", true);
            response.put("message", "Active trips retrieved successfully");
            response.put("trips", trips);
            response.put("count", trips.size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving active trips: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get trip history for a rider
     * GET /trip/rider/{riderId}/history
     */
    @GetMapping("/rider/{riderId}/history")
    public ResponseEntity<Map<String, Object>> getTripHistoryByRider(@PathVariable Long riderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<TripResponseDTO> trips = tripService.getTripHistoryByRider(riderId);
            
            response.put("success", true);
            response.put("message", "Trip history retrieved successfully");
            response.put("trips", trips);
            response.put("count", trips.size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving trip history: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get trip history for a driver
     * GET /trip/driver/{driverId}/history
     */
    @GetMapping("/driver/{driverId}/history")
    public ResponseEntity<Map<String, Object>> getTripHistoryByDriver(@PathVariable Long driverId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<TripResponseDTO> trips = tripService.getTripHistoryByDriver(driverId);
            
            response.put("success", true);
            response.put("message", "Trip history retrieved successfully");
            response.put("trips", trips);
            response.put("count", trips.size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving trip history: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
