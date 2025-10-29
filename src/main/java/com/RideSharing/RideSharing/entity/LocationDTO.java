package com.RideSharing.RideSharing.entity;

/**
 * DTO for location coordinates
 */
public class LocationDTO {
    
    private Double latitude;
    
    private Double longitude;
    
    private String address;
    
    // Constructors
    public LocationDTO() {}
    
    public LocationDTO(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public LocationDTO(Double latitude, Double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
    
    // Getters and Setters
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
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Convert to Location entity
     */
    public Location toLocation() {
        return new Location(this.latitude, this.longitude);
    }
    
    @Override
    public String toString() {
        return "LocationDTO{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                '}';
    }
}
