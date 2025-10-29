package com.RideSharing.RideSharing.context;

import com.RideSharing.RideSharing.dto.FareCalculationRequest;
import com.RideSharing.RideSharing.dto.FareCalculationResult;
import com.RideSharing.RideSharing.strategy.fare.DaytimeFareStrategy;
import com.RideSharing.RideSharing.strategy.fare.NighttimeFareStrategy;
import com.RideSharing.RideSharing.strategy.fare.WeatherBasedFareStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FareCalculatorContext
 */
@ActiveProfiles("test")
@SpringBootTest
public class FareCalculatorContextTest {
    
    private FareCalculatorContext context;
    private List<com.RideSharing.RideSharing.strategy.fare.FareStrategy> strategies;
    
    @BeforeEach
    void setUp() {
        context = new FareCalculatorContext();
        strategies = Arrays.asList(
            new DaytimeFareStrategy(),
            new NighttimeFareStrategy(),
            new WeatherBasedFareStrategy()
        );
        
        // Use reflection to set the strategies (since @Autowired won't work in test)
        try {
            java.lang.reflect.Field field = FareCalculatorContext.class.getDeclaredField("fareStrategies");
            field.setAccessible(true);
            field.set(context, strategies);
        } catch (Exception e) {
            fail("Failed to set strategies in context");
        }
    }
    
    @Test
    void testWeatherBasedStrategySelection() {
        FareCalculationRequest request = new FareCalculationRequest(
            15.0, 30, "2024-01-15T14:30:00", "RAIN", null, "ECONOMY", 2.5
        );
        
        FareCalculationResult result = context.calculateFare(request);
        
        assertNotNull(result);
        assertEquals("WEATHER_BASED", result.getStrategyUsed());
    }
    
    @Test
    void testNighttimeStrategySelection() {
        FareCalculationRequest request = new FareCalculationRequest(
            15.0, 30, "2024-01-15T23:30:00", "CLEAR", null, "ECONOMY", 2.5
        );
        
        FareCalculationResult result = context.calculateFare(request);
        
        assertNotNull(result);
        assertEquals("NIGHTTIME", result.getStrategyUsed());
    }
    
    @Test
    void testDaytimeStrategySelection() {
        FareCalculationRequest request = new FareCalculationRequest(
            15.0, 30, "2024-01-15T14:30:00", "CLEAR", null, "ECONOMY", 2.5
        );
        
        FareCalculationResult result = context.calculateFare(request);
        
        assertNotNull(result);
        assertEquals("DAYTIME", result.getStrategyUsed());
    }
    
    @Test
    void testStrategyPriority() {
        // Weather-based should take priority over nighttime
        FareCalculationRequest request = new FareCalculationRequest(
            15.0, 30, "2024-01-15T23:30:00", "RAIN", null, "ECONOMY", 2.5
        );
        
        FareCalculationResult result = context.calculateFare(request);
        
        assertNotNull(result);
        assertEquals("WEATHER_BASED", result.getStrategyUsed());
    }
    
    @Test
    void testGetAvailableStrategies() {
        List<com.RideSharing.RideSharing.strategy.fare.FareStrategy> availableStrategies = context.getAvailableStrategies();
        
        assertNotNull(availableStrategies);
        assertEquals(3, availableStrategies.size());
    }
    
    @Test
    void testGetStrategyByType() {
        com.RideSharing.RideSharing.strategy.fare.FareStrategy strategy = context.getStrategyByType("DAYTIME");
        
        assertNotNull(strategy);
        assertEquals("DAYTIME", strategy.getStrategyType());
        
        strategy = context.getStrategyByType("NONEXISTENT");
        assertNull(strategy);
    }
}

