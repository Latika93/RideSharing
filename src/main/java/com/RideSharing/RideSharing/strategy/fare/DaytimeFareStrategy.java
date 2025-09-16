package com.RideSharing.RideSharing.strategy.fare;

import com.RideSharing.RideSharing.dto.FareCalculationRequest;
import com.RideSharing.RideSharing.dto.FareCalculationResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Daytime fare calculation strategy
 * Applies standard rates during daytime hours (6 AM to 10 PM)
 */
@Component
public class DaytimeFareStrategy implements FareStrategy {
    
    private static final String STRATEGY_TYPE = "DAYTIME";
    private static final double BASE_FARE = 2.0;
    private static final double TIME_MULTIPLIER = 1.0;
    private static final double WEATHER_MULTIPLIER = 1.0;
    
    @Override
    public FareCalculationResult calculateFare(FareCalculationRequest request) {
        FareCalculationResult result = new FareCalculationResult();
        result.setStrategyUsed(STRATEGY_TYPE);
        
        // Calculate base fare
        result.setBaseFare(BASE_FARE);
        
        // Calculate distance fare
        double distanceFare = request.getDistance() * request.getBaseRate();
        result.setDistanceFare(distanceFare);
        
        // Calculate time fare (per minute)
        double timeFare = request.getDuration() * 0.5; // $0.5 per minute
        result.setTimeFare(timeFare);
        
        // Apply multipliers
        result.setTimeMultiplier(TIME_MULTIPLIER);
        result.setWeatherMultiplier(WEATHER_MULTIPLIER);
        result.setRideTypeMultiplier(getRideTypeMultiplier(request.getRideType()));
        
        // Calculate subtotal
        double subtotal = BASE_FARE + distanceFare + timeFare;
        subtotal *= result.getRideTypeMultiplier();
        result.setSubtotal(subtotal);
        
        // Apply coupon discount if available
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            // This will be handled by the coupon service
            result.setAppliedCouponCode(request.getCouponCode());
        }
        
        result.setFinalFare(subtotal);
        
        // Track applied discounts
        List<String> discounts = new ArrayList<>();
        if (result.getRideTypeMultiplier() != 1.0) {
            discounts.add("Ride type multiplier: " + result.getRideTypeMultiplier());
        }
        result.setAppliedDiscounts(discounts);
        
        return result;
    }
    
    @Override
    public String getStrategyType() {
        return STRATEGY_TYPE;
    }
    
    @Override
    public boolean isApplicable(FareCalculationRequest request) {
        if (request.getRideTime() == null) {
            return true; // Default to daytime if no time specified
        }
        
        try {
            LocalDateTime rideTime = LocalDateTime.parse(request.getRideTime());
            int hour = rideTime.getHour();
            // Daytime: 6 AM to 10 PM
            return hour >= 6 && hour < 22;
        } catch (Exception e) {
            return true; // Default to daytime if parsing fails
        }
    }
    
    private double getRideTypeMultiplier(String rideType) {
        if (rideType == null) {
            return 1.0;
        }
        
        return switch (rideType.toUpperCase()) {
            case "ECONOMY" -> 1.0;
            case "PREMIUM" -> 1.5;
            case "LUXURY" -> 2.0;
            default -> 1.0;
        };
    }
}
