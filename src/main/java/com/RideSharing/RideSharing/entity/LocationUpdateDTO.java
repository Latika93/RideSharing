package com.RideSharing.RideSharing.entity;

import java.time.LocalDateTime;

/**
 * DTO for location update messages
 */
public class LocationUpdateDTO {
    
    private String driverId;
    private String tripId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private Double speed;
    private Double heading;
    private String messageType; // "LOCATION_UPDATE", "DRIVER_ARRIVED", etc.
    
    // Constructors
    public LocationUpdateDTO() {
        this.timestamp = LocalDateTime.now();
        this.messageType = "LOCATION_UPDATE";
    }
    
    public LocationUpdateDTO(String driverId, String tripId, Double latitude, Double longitude) {
        this();
        this.driverId = driverId;
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public LocationUpdateDTO(String driverId, String tripId, Double latitude, Double longitude, 
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
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    /**
     * Convert to LocationUpdate entity
     */
    public LocationUpdate toLocationUpdate() {
        LocationUpdate update = new LocationUpdate(this.driverId, this.tripId, 
                                                 this.latitude, this.longitude, 
                                                 this.speed, this.heading);
        update.setTimestamp(this.timestamp);
        return update;
    }
    
    @Override
    public String toString() {
        return "LocationUpdateDTO{" +
                "driverId='" + driverId + '\'' +
                ", tripId='" + tripId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                ", speed=" + speed +
                ", heading=" + heading +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}

