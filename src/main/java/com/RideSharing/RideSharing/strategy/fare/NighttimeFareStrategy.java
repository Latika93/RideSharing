package com.RideSharing.RideSharing.strategy.fare;

import com.RideSharing.RideSharing.dto.FareCalculationRequest;
import com.RideSharing.RideSharing.dto.FareCalculationResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Nighttime fare calculation strategy
 * Applies higher rates during nighttime hours (10 PM to 6 AM)
 */
@Component
public class NighttimeFareStrategy implements FareStrategy {
    
    private static final String STRATEGY_TYPE = "NIGHTTIME";
    private static final double BASE_FARE = 3.0; // Higher base fare at night
    private static final double TIME_MULTIPLIER = 1.5; // 50% surcharge at night
    private static final double WEATHER_MULTIPLIER = 1.0;
    
    @Override
    public FareCalculationResult calculateFare(FareCalculationRequest request) {
        FareCalculationResult result = new FareCalculationResult();
        result.setStrategyUsed(STRATEGY_TYPE);
        
        // Calculate base fare
        result.setBaseFare(BASE_FARE);
        
        // Calculate distance fare with nighttime multiplier
        double distanceFare = request.getDistance() * request.getBaseRate() * TIME_MULTIPLIER;
        result.setDistanceFare(distanceFare);
        
        // Calculate time fare (per minute) with nighttime multiplier
        double timeFare = request.getDuration() * 0.75 * TIME_MULTIPLIER; // $0.75 per minute at night
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
            result.setAppliedCouponCode(request.getCouponCode());
        }
        
        result.setFinalFare(subtotal);
        
        // Track applied discounts
        List<String> discounts = new ArrayList<>();
        discounts.add("Nighttime surcharge: " + (TIME_MULTIPLIER - 1) * 100 + "%");
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
            return false; // Default to daytime if no time specified
        }
        
        try {
            LocalDateTime rideTime = LocalDateTime.parse(request.getRideTime());
            int hour = rideTime.getHour();
            // Nighttime: 10 PM to 6 AM
            return hour >= 22 || hour < 6;
        } catch (Exception e) {
            return false; // Default to daytime if parsing fails
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
