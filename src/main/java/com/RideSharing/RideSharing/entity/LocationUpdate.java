package com.RideSharing.RideSharing.entity;

import java.time.LocalDateTime;

/**
 * Entity for tracking location updates
 */
public class LocationUpdate {
    
    private String driverId;
    private String tripId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private Double speed; // in km/h
    private Double heading; // in degrees
    
    // Constructors
    public LocationUpdate() {
        this.timestamp = LocalDateTime.now();
    }
    
    public LocationUpdate(String driverId, String tripId, Double latitude, Double longitude) {
        this();
        this.driverId = driverId;
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public LocationUpdate(String driverId, String tripId, Double latitude, Double longitude, 
                         Double speed, Double heading) {
        this(driverId, tripId, latitude, longitude);
        this.speed = speed;
        this.heading = heading;
    }
    
    // Getters and Setters
    public String getDriverId() {
        return driverId;
    }
    
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
    public String getTripId() {
        return tripId;
    }
    
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Double getSpeed() {
        return speed;
    }
    
    public void setSpeed(Double speed) {
        this.speed = speed;
    }
    
    public Double getHeading() {
        return heading;
    }
    
    public void setHeading(Double heading) {
        this.heading = heading;
    }
    
    /**
     * Convert to Location entity
     */
    public Location toLocation() {
        return new Location(this.latitude, this.longitude);
    }
    
    @Override
    public String toString() {
        return "LocationUpdate{" +
                "driverId='" + driverId + '\'' +
                ", tripId='" + tripId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                ", speed=" + speed +
                ", heading=" + heading +
                '}';
    }
}

