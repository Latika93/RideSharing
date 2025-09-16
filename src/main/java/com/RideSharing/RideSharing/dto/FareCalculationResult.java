package com.RideSharing.RideSharing.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for fare calculation result
 */
public class FareCalculationResult {
    
    private Double baseFare;
    private Double distanceFare;
    private Double timeFare;
    private Double weatherMultiplier;
    private Double timeMultiplier;
    private Double rideTypeMultiplier;
    private Double subtotal;
    private Double discountAmount;
    private String appliedCouponCode;
    private Double finalFare;
    private String strategyUsed;
    private LocalDateTime calculatedAt;
    private List<String> appliedDiscounts;
    
    // Default constructor
    public FareCalculationResult() {
        this.calculatedAt = LocalDateTime.now();
    }
    
    // Constructor with basic parameters
    public FareCalculationResult(Double baseFare, Double distanceFare, Double timeFare, 
                               Double finalFare, String strategyUsed) {
        this();
        this.baseFare = baseFare;
        this.distanceFare = distanceFare;
        this.timeFare = timeFare;
        this.finalFare = finalFare;
        this.strategyUsed = strategyUsed;
    }
    
    // Getters and Setters
    public Double getBaseFare() {
        return baseFare;
    }
    
    public void setBaseFare(Double baseFare) {
        this.baseFare = baseFare;
    }
    
    public Double getDistanceFare() {
        return distanceFare;
    }
    
    public void setDistanceFare(Double distanceFare) {
        this.distanceFare = distanceFare;
    }
    
    public Double getTimeFare() {
        return timeFare;
    }
    
    public void setTimeFare(Double timeFare) {
        this.timeFare = timeFare;
    }
    
    public Double getWeatherMultiplier() {
        return weatherMultiplier;
    }
    
    public void setWeatherMultiplier(Double weatherMultiplier) {
        this.weatherMultiplier = weatherMultiplier;
    }
    
    public Double getTimeMultiplier() {
        return timeMultiplier;
    }
    
    public void setTimeMultiplier(Double timeMultiplier) {
        this.timeMultiplier = timeMultiplier;
    }
    
    public Double getRideTypeMultiplier() {
        return rideTypeMultiplier;
    }
    
    public void setRideTypeMultiplier(Double rideTypeMultiplier) {
        this.rideTypeMultiplier = rideTypeMultiplier;
    }
    
    public Double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
    
    public Double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public String getAppliedCouponCode() {
        return appliedCouponCode;
    }
    
    public void setAppliedCouponCode(String appliedCouponCode) {
        this.appliedCouponCode = appliedCouponCode;
    }
    
    public Double getFinalFare() {
        return finalFare;
    }
    
    public void setFinalFare(Double finalFare) {
        this.finalFare = finalFare;
    }
    
    public String getStrategyUsed() {
        return strategyUsed;
    }
    
    public void setStrategyUsed(String strategyUsed) {
        this.strategyUsed = strategyUsed;
    }
    
    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
    
    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
    
    public List<String> getAppliedDiscounts() {
        return appliedDiscounts;
    }
    
    public void setAppliedDiscounts(List<String> appliedDiscounts) {
        this.appliedDiscounts = appliedDiscounts;
    }
    
    @Override
    public String toString() {
        return "FareCalculationResult{" +
                "baseFare=" + baseFare +
                ", distanceFare=" + distanceFare +
                ", timeFare=" + timeFare +
                ", weatherMultiplier=" + weatherMultiplier +
                ", timeMultiplier=" + timeMultiplier +
                ", rideTypeMultiplier=" + rideTypeMultiplier +
                ", subtotal=" + subtotal +
                ", discountAmount=" + discountAmount +
                ", appliedCouponCode='" + appliedCouponCode + '\'' +
                ", finalFare=" + finalFare +
                ", strategyUsed='" + strategyUsed + '\'' +
                ", calculatedAt=" + calculatedAt +
                ", appliedDiscounts=" + appliedDiscounts +
                '}';
    }
}

