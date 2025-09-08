package com.RideSharing.RideSharing.entity;

/**
 * DTO for trip request creation
 */
public class TripRequestDTO {
    
    private Long riderId;
    
    private LocationDTO pickupLocation;
    
    private LocationDTO dropoffLocation;
    
    private String matchingStrategy; // Optional: "nearest", "least-busy", "high-rating"
    
    // Constructors
    public TripRequestDTO() {}
    
    public TripRequestDTO(Long riderId, LocationDTO pickupLocation, LocationDTO dropoffLocation) {
        this.riderId = riderId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
    }
    
    // Getters and Setters
    public Long getRiderId() {
        return riderId;
    }
    
    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }
    
    public LocationDTO getPickupLocation() {
        return pickupLocation;
    }
    
    public void setPickupLocation(LocationDTO pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
    
    public LocationDTO getDropoffLocation() {
        return dropoffLocation;
    }
    
    public void setDropoffLocation(LocationDTO dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }
    
    public String getMatchingStrategy() {
        return matchingStrategy;
    }
    
    public void setMatchingStrategy(String matchingStrategy) {
        this.matchingStrategy = matchingStrategy;
    }
    
    @Override
    public String toString() {
        return "TripRequestDTO{" +
                "riderId=" + riderId +
                ", pickupLocation=" + pickupLocation +
                ", dropoffLocation=" + dropoffLocation +
                ", matchingStrategy='" + matchingStrategy + '\'' +
                '}';
    }
}
