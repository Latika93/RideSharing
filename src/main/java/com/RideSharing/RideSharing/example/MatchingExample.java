package com.RideSharing.RideSharing.example;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.Location;
import com.RideSharing.RideSharing.entity.RiderProfile;
import com.RideSharing.RideSharing.entity.User;
import com.RideSharing.RideSharing.strategy.RideMatcher;
import com.RideSharing.RideSharing.strategy.NearestDriverMatcher;
import com.RideSharing.RideSharing.strategy.LeastBusyDriverMatcher;
import com.RideSharing.RideSharing.strategy.HighRatingDriverMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Example class demonstrating the usage of the driver matching system
 */
public class MatchingExample {
    
    public static void main(String[] args) {
        // Create sample data
        RiderProfile rider = createSampleRider();
        List<DriverProfile> availableDrivers = createSampleDrivers();
        
        // Create the ride matcher
        RideMatcher matcher = new RideMatcher();
        
        // Example 1: Find nearest driver
        System.out.println("=== Finding Nearest Driver ===");
        matcher.setStrategy(new NearestDriverMatcher());
        DriverProfile nearestDriver = matcher.findDriver(rider, availableDrivers);
        if (nearestDriver != null) {
            System.out.println("Nearest driver: " + nearestDriver.getUser().getUsername());
            System.out.println("Distance: " + 
                rider.getCurrentLocation().calculateDistance(nearestDriver.getCurrentLocation()) + " km");
        }
        
        // Example 2: Find least busy driver
        System.out.println("\n=== Finding Least Busy Driver ===");
        matcher.setStrategy(new LeastBusyDriverMatcher());
        DriverProfile leastBusyDriver = matcher.findDriver(rider, availableDrivers);
        if (leastBusyDriver != null) {
            System.out.println("Least busy driver: " + leastBusyDriver.getUser().getUsername());
            System.out.println("Active rides: " + leastBusyDriver.getActiveRides());
        }
        
        // Example 3: Find highest rated driver
        System.out.println("\n=== Finding Highest Rated Driver ===");
        matcher.setStrategy(new HighRatingDriverMatcher());
        DriverProfile highRatedDriver = matcher.findDriver(rider, availableDrivers);
        if (highRatedDriver != null) {
            System.out.println("Highest rated driver: " + highRatedDriver.getUser().getUsername());
            System.out.println("Rating: " + highRatedDriver.getRating());
        }
    }
    
    private static RiderProfile createSampleRider() {
        User riderUser = new User();
        riderUser.setUsername("Latika");
        riderUser.setEmail("latika@example.com");
        
        RiderProfile rider = new RiderProfile();
        rider.setUser(riderUser);
        rider.setCurrentLocation(new Location(28.61, 77.23)); // Delhi coordinates
        return rider;
    }
    
    private static List<DriverProfile> createSampleDrivers() {
        List<DriverProfile> drivers = new ArrayList<>();
        
        // Driver 1 - Nearest to rider
        User driver1User = new User();
        driver1User.setUsername("Rajesh");
        driver1User.setEmail("rajesh@example.com");
        
        DriverProfile driver1 = new DriverProfile();
        driver1.setUser(driver1User);
        driver1.setCurrentLocation(new Location(28.62, 77.24)); // Very close to rider
        driver1.setRating(4.2);
        driver1.setActiveRides(2);
        driver1.setAvailable(true);
        drivers.add(driver1);
        
        // Driver 2 - Least busy
        User driver2User = new User();
        driver2User.setUsername("Priya");
        driver2User.setEmail("priya@example.com");
        
        DriverProfile driver2 = new DriverProfile();
        driver2.setUser(driver2User);
        driver2.setCurrentLocation(new Location(28.70, 77.30)); // Further from rider
        driver2.setRating(4.5);
        driver2.setActiveRides(0); // Least busy
        driver2.setAvailable(true);
        drivers.add(driver2);
        
        // Driver 3 - Highest rated
        User driver3User = new User();
        driver3User.setUsername("Amit");
        driver3User.setEmail("amit@example.com");
        
        DriverProfile driver3 = new DriverProfile();
        driver3.setUser(driver3User);
        driver3.setCurrentLocation(new Location(28.65, 77.25)); // Medium distance
        driver3.setRating(4.8); // Highest rating
        driver3.setActiveRides(1);
        driver3.setAvailable(true);
        drivers.add(driver3);
        
        return drivers;
    }
}
