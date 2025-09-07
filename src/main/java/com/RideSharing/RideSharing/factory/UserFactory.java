package com.RideSharing.RideSharing.factory;

import com.RideSharing.RideSharing.entity.*;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User createRider(String username, String email, String password, String phoneNumber, 
                           String paymentMethod, String preferences) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("RIDER");
        user.setEnabled(false);

        RiderProfile riderProfile = new RiderProfile();
        riderProfile.setPhoneNumber(phoneNumber);
        riderProfile.setPaymentMethod(paymentMethod);
        riderProfile.setPreferences(preferences);
        riderProfile.setUser(user);

        user.setRiderProfile(riderProfile);
        return user;
    }

    public User createDriver(String username, String email, String password, String licenseNumber,
                            String vehicleMake, String vehicleModel, String vehicleYear,
                            String vehicleColor, String vehiclePlateNumber) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("DRIVER");
        user.setEnabled(false);

        DriverProfile driverProfile = new DriverProfile();
        driverProfile.setLicenseNumber(licenseNumber);
        driverProfile.setVehicleMake(vehicleMake);
        driverProfile.setVehicleModel(vehicleModel);
        driverProfile.setVehicleYear(vehicleYear);
        driverProfile.setVehicleColor(vehicleColor);
        driverProfile.setVehiclePlateNumber(vehiclePlateNumber);
        driverProfile.setAvailable(false); // Initially not available
        driverProfile.setUser(user);

        user.setDriverProfile(driverProfile);
        return user;
    }

    public User createAdmin(String username, String email, String password, String privileges) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("ADMIN");
        user.setEnabled(false);

        AdminProfile adminProfile = new AdminProfile();
        adminProfile.setPrivileges(privileges);
        adminProfile.setUser(user);

        user.setAdminProfile(adminProfile);
        return user;
    }
}
