package com.RideSharing.RideSharing.service;

import com.RideSharing.RideSharing.entity.LocationUpdate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocationTrackingService {

    // In-memory storage for location updates (in production, use Redis or database)
    private final Map<String, List<LocationUpdate>> driverLocations = new ConcurrentHashMap<>();
    private final Map<String, LocationUpdate> latestDriverLocations = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastUpdateTimes = new ConcurrentHashMap<>();

    // Configuration constants
    private static final int MAX_LOCATION_HISTORY = 100; // Keep last 100 updates per driver
    private static final long MIN_UPDATE_INTERVAL_MS = 2000; // Minimum 2 seconds between updates
    private static final double MIN_DISTANCE_METERS = 10.0; // Minimum 10 meters movement

    /**
     * Store a location update for a driver
     */
    public void storeLocationUpdate(LocationUpdate locationUpdate) {
        String driverId = locationUpdate.getDriverId();
        
        if (driverId == null) {
            return;
        }

        // Check if enough time has passed since last update
        if (!shouldUpdateLocation(driverId, locationUpdate)) {
            return;
        }

        // Store the update
        driverLocations.computeIfAbsent(driverId, k -> new ArrayList<>()).add(locationUpdate);
        latestDriverLocations.put(driverId, locationUpdate);
        lastUpdateTimes.put(driverId, LocalDateTime.now());

        // Clean up old location history
        cleanupLocationHistory(driverId);
    }

    /**
     * Get the latest location for a driver
     */
    public LocationUpdate getLatestDriverLocation(String driverId) {
        return latestDriverLocations.get(driverId);
    }

    /**
     * Get location history for a driver
     */
    public List<LocationUpdate> getDriverLocationHistory(String driverId, int limit) {
        List<LocationUpdate> history = driverLocations.get(driverId);
        if (history == null) {
            return new ArrayList<>();
        }
        
        int startIndex = Math.max(0, history.size() - limit);
        return new ArrayList<>(history.subList(startIndex, history.size()));
    }

    /**
     * Get all active drivers (drivers with recent location updates)
     */
    public Set<String> getActiveDrivers() {
        Set<String> activeDrivers = new HashSet<>();
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5); // Active within last 5 minutes
        
        for (Map.Entry<String, LocalDateTime> entry : lastUpdateTimes.entrySet()) {
            if (entry.getValue().isAfter(cutoff)) {
                activeDrivers.add(entry.getKey());
            }
        }
        
        return activeDrivers;
    }

    /**
     * Get drivers near a specific location
     */
    public List<LocationUpdate> getDriversNearLocation(double latitude, double longitude, double radiusKm) {
        List<LocationUpdate> nearbyDrivers = new ArrayList<>();
        
        for (LocationUpdate location : latestDriverLocations.values()) {
            if (location.getLatitude() != null && location.getLongitude() != null) {
                double distance = calculateDistance(latitude, longitude, 
                                                 location.getLatitude(), location.getLongitude());
                if (distance <= radiusKm) {
                    nearbyDrivers.add(location);
                }
            }
        }
        
        return nearbyDrivers;
    }

    /**
     * Check if a location update should be processed
     */
    private boolean shouldUpdateLocation(String driverId, LocationUpdate newUpdate) {
        LocationUpdate lastUpdate = latestDriverLocations.get(driverId);
        LocalDateTime lastTime = lastUpdateTimes.get(driverId);
        
        // If no previous update, allow this one
        if (lastUpdate == null || lastTime == null) {
            return true;
        }
        
        // Check time interval
        if (lastTime.isAfter(LocalDateTime.now().minusNanos(MIN_UPDATE_INTERVAL_MS * 1_000_000))) {
            return false;
        }
        
        // Check distance moved
        if (lastUpdate.getLatitude() != null && lastUpdate.getLongitude() != null &&
            newUpdate.getLatitude() != null && newUpdate.getLongitude() != null) {
            
            double distance = calculateDistance(
                lastUpdate.getLatitude(), lastUpdate.getLongitude(),
                newUpdate.getLatitude(), newUpdate.getLongitude()
            );
            
            // Convert km to meters
            double distanceMeters = distance * 1000;
            if (distanceMeters < MIN_DISTANCE_METERS) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Clean up old location history to prevent memory issues
     */
    private void cleanupLocationHistory(String driverId) {
        List<LocationUpdate> history = driverLocations.get(driverId);
        if (history != null && history.size() > MAX_LOCATION_HISTORY) {
            // Keep only the most recent updates
            int removeCount = history.size() - MAX_LOCATION_HISTORY;
            for (int i = 0; i < removeCount; i++) {
                history.remove(0);
            }
        }
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Remove driver location data (when driver goes offline)
     */
    public void removeDriverLocation(String driverId) {
        driverLocations.remove(driverId);
        latestDriverLocations.remove(driverId);
        lastUpdateTimes.remove(driverId);
    }

    /**
     * Get statistics about location tracking
     */
    public Map<String, Object> getLocationTrackingStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeDrivers", getActiveDrivers().size());
        stats.put("totalDrivers", latestDriverLocations.size());
        stats.put("lastUpdateTime", LocalDateTime.now());
        return stats;
    }
}

