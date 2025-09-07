package com.RideSharing.RideSharing.entity;

public class RiderRegistrationDTO {
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String paymentMethod;
    private String preferences;

    public RiderRegistrationDTO() {
    }

    public RiderRegistrationDTO(String username, String email, String password, 
                               String phoneNumber, String paymentMethod, String preferences) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.paymentMethod = paymentMethod;
        this.preferences = preferences;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
