package com.RideSharing.RideSharing.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Location {
    
    private Double latitude;
    private Double longitude;
    
    public Location() {
    }
    
    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
    
    /**
     * Calculate distance between two locations using Haversine formula
     * @param other The other location
     * @return Distance in kilometers
     */
    public double calculateDistance(Location other) {
        if (other == null || this.latitude == null || this.longitude == null || 
            other.latitude == null || other.longitude == null) {
            return Double.MAX_VALUE;
        }
        
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
