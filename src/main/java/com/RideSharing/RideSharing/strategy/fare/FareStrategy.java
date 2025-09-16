package com.RideSharing.RideSharing.strategy.fare;

import com.RideSharing.RideSharing.dto.FareCalculationRequest;
import com.RideSharing.RideSharing.dto.FareCalculationResult;

/**
 * Strategy interface for fare calculation algorithms
 * Follows the Strategy pattern to allow different fare calculation methods
 */
public interface FareStrategy {
    
    /**
     * Calculate fare based on the specific strategy
     * @param request The fare calculation request containing ride details
     * @return The calculated fare result
     */
    FareCalculationResult calculateFare(FareCalculationRequest request);
    
    /**
     * Get the strategy type identifier
     * @return The strategy type
     */
    String getStrategyType();
    
    /**
     * Check if this strategy is applicable for the given request
     * @param request The fare calculation request
     * @return true if this strategy should be used, false otherwise
     */
    boolean isApplicable(FareCalculationRequest request);
}

