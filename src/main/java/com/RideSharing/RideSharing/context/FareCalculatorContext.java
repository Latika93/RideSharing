package com.RideSharing.RideSharing.context;

import com.RideSharing.RideSharing.dto.FareCalculationRequest;
import com.RideSharing.RideSharing.dto.FareCalculationResult;
import com.RideSharing.RideSharing.strategy.fare.FareStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Context class for the Strategy pattern in fare calculation
 * Manages the fare calculation strategy and provides a unified interface
 */
@Component
public class FareCalculatorContext {
    
    @Autowired
    private List<FareStrategy> fareStrategies;
    
    /**
     * Calculate fare using the most appropriate strategy based on the request
     * @param request The fare calculation request
     * @return The calculated fare result
     */
    public FareCalculationResult calculateFare(FareCalculationRequest request) {
        FareStrategy strategy = selectStrategy(request);
        
        if (strategy == null) {
            throw new IllegalStateException("No applicable fare strategy found for the given request");
        }
        
        return strategy.calculateFare(request);
    }
    
    /**
     * Select the most appropriate strategy based on the request parameters
     * Priority: Weather-based > Nighttime > Daytime
     * @param request The fare calculation request
     * @return The selected strategy
     */
    private FareStrategy selectStrategy(FareCalculationRequest request) {
        // Priority 1: Weather-based strategy (highest priority for surge pricing)
        for (FareStrategy strategy : fareStrategies) {
            if (strategy.isApplicable(request) && 
                "WEATHER_BASED".equals(strategy.getStrategyType())) {
                return strategy;
            }
        }
        
        // Priority 2: Nighttime strategy
        for (FareStrategy strategy : fareStrategies) {
            if (strategy.isApplicable(request) && 
                "NIGHTTIME".equals(strategy.getStrategyType())) {
                return strategy;
            }
        }
        
        // Priority 3: Daytime strategy (default fallback)
        for (FareStrategy strategy : fareStrategies) {
            if (strategy.isApplicable(request) && 
                "DAYTIME".equals(strategy.getStrategyType())) {
                return strategy;
            }
        }
        
        // If no strategy is applicable, return the first available strategy as fallback
        return fareStrategies.isEmpty() ? null : fareStrategies.get(0);
    }
    
    /**
     * Get all available strategies
     * @return List of all fare strategies
     */
    public List<FareStrategy> getAvailableStrategies() {
        return fareStrategies;
    }
    
    /**
     * Get a specific strategy by type
     * @param strategyType The strategy type to find
     * @return The strategy if found, null otherwise
     */
    public FareStrategy getStrategyByType(String strategyType) {
        return fareStrategies.stream()
                .filter(strategy -> strategyType.equals(strategy.getStrategyType()))
                .findFirst()
                .orElse(null);
    }
}

