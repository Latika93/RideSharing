package com.RideSharing.RideSharing.strategy.fare;

import com.RideSharing.RideSharing.dto.FareCalculationRequest;
import com.RideSharing.RideSharing.dto.FareCalculationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Weather-based fare calculation strategy
 * Applies surge pricing based on weather conditions
 */
@Component
public class WeatherBasedFareStrategy implements FareStrategy {
    
    private static final String STRATEGY_TYPE = "WEATHER_BASED";
    private static final double BASE_FARE = 2.0;
    private static final double TIME_MULTIPLIER = 1.0;
    
    @Override
    public FareCalculationResult calculateFare(FareCalculationRequest request) {
        FareCalculationResult result = new FareCalculationResult();
        result.setStrategyUsed(STRATEGY_TYPE);
        
        // Calculate base fare
        result.setBaseFare(BASE_FARE);
        
        // Get weather multiplier
        double weatherMultiplier = getWeatherMultiplier(request.getWeatherCondition());
        result.setWeatherMultiplier(weatherMultiplier);
        
        // Calculate distance fare with weather multiplier
        double distanceFare = request.getDistance() * request.getBaseRate() * weatherMultiplier;
        result.setDistanceFare(distanceFare);
        
        // Calculate time fare (per minute)
        double timeFare = request.getDuration() * 0.5;
        result.setTimeFare(timeFare);
        
        // Apply multipliers
        result.setTimeMultiplier(TIME_MULTIPLIER);
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
        if (weatherMultiplier > 1.0) {
            discounts.add("Weather surge pricing: " + (weatherMultiplier - 1) * 100 + "%");
        }
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
        if (request.getWeatherCondition() == null) {
            return false;
        }
        
        String weather = request.getWeatherCondition().toUpperCase();
        // Apply weather-based pricing for adverse conditions
        return weather.equals("RAIN") || weather.equals("SNOW") || 
               weather.equals("STORM") || weather.equals("FOG") ||
               weather.equals("HEAVY_RAIN") || weather.equals("BLIZZARD");
    }
    
    private double getWeatherMultiplier(String weatherCondition) {
        if (weatherCondition == null) {
            return 1.0;
        }
        
        return switch (weatherCondition.toUpperCase()) {
            case "CLEAR", "SUNNY", "PARTLY_CLOUDY" -> 1.0;
            case "CLOUDY", "OVERCAST" -> 1.1; // 10% surcharge
            case "RAIN", "DRIZZLE" -> 1.3; // 30% surcharge
            case "HEAVY_RAIN", "STORM" -> 1.5; // 50% surcharge
            case "SNOW", "LIGHT_SNOW" -> 1.4; // 40% surcharge
            case "BLIZZARD", "HEAVY_SNOW" -> 1.8; // 80% surcharge
            case "FOG", "MIST" -> 1.2; // 20% surcharge
            default -> 1.0;
        };
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
