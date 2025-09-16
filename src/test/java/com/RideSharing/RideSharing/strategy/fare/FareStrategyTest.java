package com.RideSharing.RideSharing.strategy.fare;

import com.RideSharing.RideSharing.dto.FareCalculationRequest;
import com.RideSharing.RideSharing.dto.FareCalculationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for fare calculation strategies
 */
@SpringBootTest
public class FareStrategyTest {
    
    private DaytimeFareStrategy daytimeStrategy;
    private NighttimeFareStrategy nighttimeStrategy;
    private WeatherBasedFareStrategy weatherStrategy;
    
    @BeforeEach
    void setUp() {
        daytimeStrategy = new DaytimeFareStrategy();
        nighttimeStrategy = new NighttimeFareStrategy();
        weatherStrategy = new WeatherBasedFareStrategy();
    }
    
    @Test
    void testDaytimeFareStrategy() {
        FareCalculationRequest request = new FareCalculationRequest(
            15.0, 30, "2024-01-15T14:30:00", "CLEAR", null, "ECONOMY", 2.5
        );
        
        FareCalculationResult result = daytimeStrategy.calculateFare(request);
        
        assertNotNull(result);
        assertEquals("DAYTIME", result.getStrategyUsed());
        assertEquals(2.0, result.getBaseFare());
        assertEquals(37.5, result.getDistanceFare()); // 15.0 * 2.5
        assertEquals(15.0, result.getTimeFare()); // 30 * 0.5
        assertEquals(1.0, result.getTimeMultiplier());
        assertEquals(1.0, result.getWeatherMultiplier());
        assertEquals(1.0, result.getRideTypeMultiplier());
    }
    
    @Test
    void testNighttimeFareStrategy() {
        FareCalculationRequest request = new FareCalculationRequest(
            15.0, 30, "2024-01-15T23:30:00", "CLEAR", null, "ECONOMY", 2.5
        );
        
        FareCalculationResult result = nighttimeStrategy.calculateFare(request);
        
        assertNotNull(result);
        assertEquals("NIGHTTIME", result.getStrategyUsed());
        assertEquals(3.0, result.getBaseFare());
        assertEquals(56.25, result.getDistanceFare()); // 15.0 * 2.5 * 1.5
        assertEquals(33.75, result.getTimeFare()); // 30 * 0.75 * 1.5
        assertEquals(1.5, result.getTimeMultiplier());
    }
    
    @Test
    void testWeatherBasedFareStrategy() {
        FareCalculationRequest request = new FareCalculationRequest(
            15.0, 30, "2024-01-15T14:30:00", "RAIN", null, "ECONOMY", 2.5
        );
        
        FareCalculationResult result = weatherStrategy.calculateFare(request);
        
        assertNotNull(result);
        assertEquals("WEATHER_BASED", result.getStrategyUsed());
        assertEquals(2.0, result.getBaseFare());
        assertEquals(48.75, result.getDistanceFare()); // 15.0 * 2.5 * 1.3
        assertEquals(15.0, result.getTimeFare()); // 30 * 0.5
        assertEquals(1.3, result.getWeatherMultiplier());
    }
    
    @Test
    void testStrategyApplicability() {
        // Test daytime applicability
        FareCalculationRequest daytimeRequest = new FareCalculationRequest(
            15.0, 30, "2024-01-15T14:30:00", "CLEAR", null, "ECONOMY", 2.5
        );
        assertTrue(daytimeStrategy.isApplicable(daytimeRequest));
        assertFalse(nighttimeStrategy.isApplicable(daytimeRequest));
        
        // Test nighttime applicability
        FareCalculationRequest nighttimeRequest = new FareCalculationRequest(
            15.0, 30, "2024-01-15T23:30:00", "CLEAR", null, "ECONOMY", 2.5
        );
        assertFalse(daytimeStrategy.isApplicable(nighttimeRequest));
        assertTrue(nighttimeStrategy.isApplicable(nighttimeRequest));
        
        // Test weather-based applicability
        FareCalculationRequest weatherRequest = new FareCalculationRequest(
            15.0, 30, "2024-01-15T14:30:00", "RAIN", null, "ECONOMY", 2.5
        );
        assertTrue(weatherStrategy.isApplicable(weatherRequest));
        
        FareCalculationRequest clearWeatherRequest = new FareCalculationRequest(
            15.0, 30, "2024-01-15T14:30:00", "CLEAR", null, "ECONOMY", 2.5
        );
        assertFalse(weatherStrategy.isApplicable(clearWeatherRequest));
    }
    
    @Test
    void testRideTypeMultipliers() {
        FareCalculationRequest economyRequest = new FareCalculationRequest(
            10.0, 20, "2024-01-15T14:30:00", "CLEAR", null, "ECONOMY", 2.5
        );
        
        FareCalculationRequest premiumRequest = new FareCalculationRequest(
            10.0, 20, "2024-01-15T14:30:00", "CLEAR", null, "PREMIUM", 2.5
        );
        
        FareCalculationRequest luxuryRequest = new FareCalculationRequest(
            10.0, 20, "2024-01-15T14:30:00", "CLEAR", null, "LUXURY", 2.5
        );
        
        FareCalculationResult economyResult = daytimeStrategy.calculateFare(economyRequest);
        FareCalculationResult premiumResult = daytimeStrategy.calculateFare(premiumRequest);
        FareCalculationResult luxuryResult = daytimeStrategy.calculateFare(luxuryRequest);
        
        assertEquals(1.0, economyResult.getRideTypeMultiplier());
        assertEquals(1.5, premiumResult.getRideTypeMultiplier());
        assertEquals(2.0, luxuryResult.getRideTypeMultiplier());
    }
}

