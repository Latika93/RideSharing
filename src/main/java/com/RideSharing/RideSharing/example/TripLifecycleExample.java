package com.RideSharing.RideSharing.example;

import com.RideSharing.RideSharing.entity.*;

/**
 * Example demonstrating the complete trip lifecycle with FSM
 */
public class TripLifecycleExample {
    
    public static void main(String[] args) {
        System.out.println("=== Trip Lifecycle FSM Example ===\n");
        
        // Create sample entities
        User riderUser = createSampleRider();
        User driverUser = createSampleDriver();
        
        RiderProfile rider = createRiderProfile(riderUser);
        DriverProfile driver = createDriverProfile(driverUser);
        
        // Create trip request
        Location pickupLocation = new Location(28.61, 77.23); // Delhi
        Location dropoffLocation = new Location(28.70, 77.30); // Delhi
        
        Trip trip = new Trip(rider, pickupLocation, dropoffLocation);
        trip.setDistanceKm(pickupLocation.calculateDistance(dropoffLocation));
        
        System.out.println("1. Trip Created:");
        System.out.println("   State: " + trip.getState());
        System.out.println("   Distance: " + String.format("%.2f km", trip.getDistanceKm()));
        System.out.println("   Requested At: " + trip.getRequestedAt());
        System.out.println();
        
        // Driver accepts trip
        try {
            trip.assignDriver(driver);
            System.out.println("2. Driver Accepted:");
            System.out.println("   State: " + trip.getState());
            System.out.println("   Driver: " + driver.getUser().getUsername());
            System.out.println("   Accepted At: " + trip.getAcceptedAt());
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error accepting trip: " + e.getMessage());
        }
        
        // Start trip
        try {
            trip.startTrip();
            System.out.println("3. Trip Started:");
            System.out.println("   State: " + trip.getState());
            System.out.println("   Started At: " + trip.getStartedAt());
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error starting trip: " + e.getMessage());
        }
        
        // Complete trip
        try {
            trip.completeTrip();
            System.out.println("4. Trip Completed:");
            System.out.println("   State: " + trip.getState());
            System.out.println("   Completed At: " + trip.getCompletedAt());
            System.out.println("   Fare Amount: ₹" + String.format("%.2f", trip.getFareAmount()));
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error completing trip: " + e.getMessage());
        }
        
        // Demonstrate invalid transitions
        System.out.println("5. Testing Invalid Transitions:");
        try {
            trip.startTrip(); // Should fail - already completed
        } catch (Exception e) {
            System.out.println("   ✓ Correctly rejected: " + e.getMessage());
        }
        
        try {
            trip.assignDriver(driver); // Should fail - already completed
        } catch (Exception e) {
            System.out.println("   ✓ Correctly rejected: " + e.getMessage());
        }
        
        // Demonstrate cancellation example
        System.out.println("\n6. Cancellation Example:");
        Trip cancelledTrip = new Trip(rider, pickupLocation, dropoffLocation);
        try {
            cancelledTrip.cancelTrip("Rider changed mind", CancelledBy.RIDER);
            System.out.println("   State: " + cancelledTrip.getState());
            System.out.println("   Cancelled At: " + cancelledTrip.getCancelledAt());
            System.out.println("   Cancelled By: " + cancelledTrip.getCancelledBy());
            System.out.println("   Reason: " + cancelledTrip.getCancellationReason());
        } catch (Exception e) {
            System.out.println("Error cancelling trip: " + e.getMessage());
        }
        
        System.out.println("\n=== Trip Lifecycle Complete ===");
    }
    
    private static User createSampleRider() {
        User user = new User();
        user.setUsername("Latika");
        user.setEmail("latika@example.com");
        user.setRole("RIDER");
        user.setEnabled(true);
        return user;
    }
    
    private static User createSampleDriver() {
        User user = new User();
        user.setUsername("Rajesh");
        user.setEmail("rajesh@example.com");
        user.setRole("DRIVER");
        user.setEnabled(true);
        return user;
    }
    
    private static RiderProfile createRiderProfile(User user) {
        RiderProfile rider = new RiderProfile();
        rider.setUser(user);
        rider.setPhoneNumber("+91-9876543210");
        rider.setPaymentMethod("UPI");
        rider.setCurrentLocation(new Location(28.61, 77.23));
        return rider;
    }
    
    private static DriverProfile createDriverProfile(User user) {
        DriverProfile driver = new DriverProfile();
        driver.setUser(user);
        driver.setLicenseNumber("DL123456789");
        driver.setVehicleMake("Maruti");
        driver.setVehicleModel("Swift");
        driver.setVehicleYear("2020");
        driver.setVehicleColor("White");
        driver.setVehiclePlateNumber("DL-01-AB-1234");
        driver.setAvailable(true);
        driver.setRating(4.5);
        driver.setActiveRides(0);
        driver.setCurrentLocation(new Location(28.62, 77.24));
        return driver;
    }
}
