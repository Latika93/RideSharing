package com.RideSharing.RideSharing.entity;

import java.time.LocalDateTime;

/**
 * DTO for trip response data
 */
public class TripResponseDTO {
    
    private Long id;
    private TripState state;
    private Long riderId;
    private String riderName;
    private Long driverId;
    private String driverName;
    private LocationDTO pickupLocation;
    private LocationDTO dropoffLocation;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private Double fareAmount;
    private Double distanceKm;
    private Integer estimatedDurationMinutes;
    private String cancellationReason;
    private CancelledBy cancelledBy;
    
    // Constructors
    public TripResponseDTO() {}
    
    public TripResponseDTO(Trip trip) {
        this.id = trip.getId();
        this.state = trip.getState();
        this.riderId = trip.getRider() != null ? trip.getRider().getId() : null;
        this.riderName = trip.getRider() != null && trip.getRider().getUser() != null ? 
                        trip.getRider().getUser().getUsername() : null;
        this.driverId = trip.getDriver() != null ? trip.getDriver().getId() : null;
        this.driverName = trip.getDriver() != null && trip.getDriver().getUser() != null ? 
                         trip.getDriver().getUser().getUsername() : null;
        this.pickupLocation = trip.getPickupLocation() != null ? 
                             new LocationDTO(trip.getPickupLocation().getLatitude(), 
                                           trip.getPickupLocation().getLongitude()) : null;
        this.dropoffLocation = trip.getDropoffLocation() != null ? 
                              new LocationDTO(trip.getDropoffLocation().getLatitude(), 
                                            trip.getDropoffLocation().getLongitude()) : null;
        this.requestedAt = trip.getRequestedAt();
        this.acceptedAt = trip.getAcceptedAt();
        this.startedAt = trip.getStartedAt();
        this.completedAt = trip.getCompletedAt();
        this.cancelledAt = trip.getCancelledAt();
        this.fareAmount = trip.getFareAmount();
        this.distanceKm = trip.getDistanceKm();
        this.estimatedDurationMinutes = trip.getEstimatedDurationMinutes();
        this.cancellationReason = trip.getCancellationReason();
        this.cancelledBy = trip.getCancelledBy();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TripState getState() {
        return state;
    }
    
    public void setState(TripState state) {
        this.state = state;
    }
    
    public Long getRiderId() {
        return riderId;
    }
    
    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }
    
    public String getRiderName() {
        return riderName;
    }
    
    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }
    
    public Long getDriverId() {
        return driverId;
    }
    
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
    
    public String getDriverName() {
        return driverName;
    }
    
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    
    public LocationDTO getPickupLocation() {
        return pickupLocation;
    }
    
    public void setPickupLocation(LocationDTO pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
    
    public LocationDTO getDropoffLocation() {
        return dropoffLocation;
    }
    
    public void setDropoffLocation(LocationDTO dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
    
    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }
    
    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public Double getFareAmount() {
        return fareAmount;
    }
    
    public void setFareAmount(Double fareAmount) {
        this.fareAmount = fareAmount;
    }
    
    public Double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }
    
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public CancelledBy getCancelledBy() {
        return cancelledBy;
    }
    
    public void setCancelledBy(CancelledBy cancelledBy) {
        this.cancelledBy = cancelledBy;
    }
    
    @Override
    public String toString() {
        return "TripResponseDTO{" +
                "id=" + id +
                ", state=" + state +
                ", riderId=" + riderId +
                ", driverId=" + driverId +
                ", requestedAt=" + requestedAt +
                '}';
    }
}
