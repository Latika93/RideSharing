package com.RideSharing.RideSharing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;


@Entity
public class User {

  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long userId;

  private String username;

  private String email;

  private String password;

  private String role;

  private boolean isEnabled;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
  private DriverProfile driverProfile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
  private RiderProfile riderProfile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
  private AdminProfile adminProfile;

  public User(Long userId, String username, String email, String password, String role, boolean isEnabled) {
    this.userId = userId;
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
    this.isEnabled = isEnabled;
  }

  public User() {

  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @JsonIgnore
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public DriverProfile getDriverProfile() {
    return driverProfile;
  }

  public void setDriverProfile(DriverProfile driverProfile) {
    this.driverProfile = driverProfile;
  }

  public RiderProfile getRiderProfile() {
    return riderProfile;
  }

  public void setRiderProfile(RiderProfile riderProfile) {
    this.riderProfile = riderProfile;
  }

  public AdminProfile getAdminProfile() {
    return adminProfile;
  }

  public void setAdminProfile(AdminProfile adminProfile) {
    this.adminProfile = adminProfile;
  }
}
