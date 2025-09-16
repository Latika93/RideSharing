package com.RideSharing.RideSharing.dto;


/**
 * DTO for fare calculation request
 */
public class FareCalculationRequest {
    
    private Double distance; // in kilometers
    private Integer duration; // in minutes
    private String rideTime; // ISO format datetime string
    private String weatherCondition; // CLEAR, RAIN, SNOW, etc.
    private String couponCode; // optional coupon code
    private String rideType; // ECONOMY, PREMIUM, LUXURY
    private Double baseRate; // base rate per km
    
    // Default constructor
    public FareCalculationRequest() {}
    
    // Constructor with all parameters
    public FareCalculationRequest(Double distance, Integer duration, String rideTime, 
                                String weatherCondition, String couponCode, 
                                String rideType, Double baseRate) {
        this.distance = distance;
        this.duration = duration;
        this.rideTime = rideTime;
        this.weatherCondition = weatherCondition;
        this.couponCode = couponCode;
        this.rideType = rideType;
        this.baseRate = baseRate;
    }
    
    // Getters and Setters
    public Double getDistance() {
        return distance;
    }
    
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getRideTime() {
        return rideTime;
    }
    
    public void setRideTime(String rideTime) {
        this.rideTime = rideTime;
    }
    
    public String getWeatherCondition() {
        return weatherCondition;
    }
    
    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }
    
    public String getCouponCode() {
        return couponCode;
    }
    
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
    
    public String getRideType() {
        return rideType;
    }
    
    public void setRideType(String rideType) {
        this.rideType = rideType;
    }
    
    public Double getBaseRate() {
        return baseRate;
    }
    
    public void setBaseRate(Double baseRate) {
        this.baseRate = baseRate;
    }
    
    @Override
    public String toString() {
        return "FareCalculationRequest{" +
                "distance=" + distance +
                ", duration=" + duration +
                ", rideTime='" + rideTime + '\'' +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", couponCode='" + couponCode + '\'' +
                ", rideType='" + rideType + '\'' +
                ", baseRate=" + baseRate +
                '}';
    }
}
