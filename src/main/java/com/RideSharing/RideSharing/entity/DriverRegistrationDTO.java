package com.RideSharing.RideSharing.entity;

public class DriverRegistrationDTO {
    private String username;
    private String email;
    private String password;
    private String licenseNumber;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleYear;
    private String vehicleColor;
    private String vehiclePlateNumber;

    public DriverRegistrationDTO() {
    }

    public DriverRegistrationDTO(String username, String email, String password, 
                                String licenseNumber, String vehicleMake, String vehicleModel,
                                String vehicleYear, String vehicleColor, String vehiclePlateNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.licenseNumber = licenseNumber;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleYear = vehicleYear;
        this.vehicleColor = vehicleColor;
        this.vehiclePlateNumber = vehiclePlateNumber;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(String vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getVehiclePlateNumber() {
        return vehiclePlateNumber;
    }

    public void setVehiclePlateNumber(String vehiclePlateNumber) {
        this.vehiclePlateNumber = vehiclePlateNumber;
    }
}
