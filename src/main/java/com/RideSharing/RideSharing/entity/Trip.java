package com.RideSharing.RideSharing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Trip entity representing a ride with Finite State Machine (FSM) lifecycle
 * States: REQUESTED → ACCEPTED → STARTED → COMPLETED
 * Alternative: REQUESTED/ACCEPTED → CANCELLED
 */
@Entity
@Table(name = "trips")
public class Trip {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripState state;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id", nullable = false)
    @JsonIgnore
    private RiderProfile rider;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    @JsonIgnore
    private DriverProfile driver;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "latitude", column = @Column(name = "pickup_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "pickup_longitude"))
    })
    private Location pickupLocation;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "latitude", column = @Column(name = "dropoff_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "dropoff_longitude"))
    })
    private Location dropoffLocation;
    
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
    
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "fare_amount")
    private Double fareAmount;
    
    @Column(name = "distance_km")
    private Double distanceKm;
    
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column(name = "cancelled_by")
    @Enumerated(EnumType.STRING)
    private CancelledBy cancelledBy;
    
    // Constructors
    public Trip() {
        this.state = TripState.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }
    
    public Trip(RiderProfile rider, Location pickupLocation, Location dropoffLocation) {
        this();
        this.rider = rider;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
    }
    
    // State transition methods
    public boolean canTransitionTo(TripState newState) {
        return switch (this.state) {
            case REQUESTED -> newState == TripState.ACCEPTED || newState == TripState.CANCELLED;
            case ACCEPTED -> newState == TripState.STARTED || newState == TripState.CANCELLED;
            case STARTED -> newState == TripState.COMPLETED;
            case COMPLETED, CANCELLED -> false; // Terminal states
        };
    }
    
    public void transitionTo(TripState newState) {
        if (!canTransitionTo(newState)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.state, newState)
            );
        }
        
        this.state = newState;
        LocalDateTime now = LocalDateTime.now();
        
        switch (newState) {
            case ACCEPTED -> this.acceptedAt = now;
            case STARTED -> this.startedAt = now;
            case COMPLETED -> this.completedAt = now;
            case CANCELLED -> this.cancelledAt = now;
        }
    }
    
    // Business logic methods
    public void assignDriver(DriverProfile driver) {
        if (this.state != TripState.REQUESTED) {
            throw new IllegalStateException("Can only assign driver to REQUESTED trips");
        }
        this.driver = driver;
        transitionTo(TripState.ACCEPTED);
    }
    
    public void startTrip() {
        if (this.state != TripState.ACCEPTED) {
            throw new IllegalStateException("Can only start ACCEPTED trips");
        }
        transitionTo(TripState.STARTED);
    }
    
    public void completeTrip() {
        if (this.state != TripState.STARTED) {
            throw new IllegalStateException("Can only complete STARTED trips");
        }
        transitionTo(TripState.COMPLETED);
        calculateFare();
    }
    
    public void cancelTrip(String reason, CancelledBy cancelledBy) {
        if (this.state != TripState.REQUESTED && this.state != TripState.ACCEPTED) {
            throw new IllegalStateException("Can only cancel REQUESTED or ACCEPTED trips");
        }
        this.cancellationReason = reason;
        this.cancelledBy = cancelledBy;
        transitionTo(TripState.CANCELLED);
    }
    
    private void calculateFare() {
        if (this.distanceKm != null) {
            // Simple fare calculation: base fare + distance-based fare
            double baseFare = 50.0; // Base fare in rupees
            double perKmRate = 15.0; // Per kilometer rate
            this.fareAmount = baseFare + (this.distanceKm * perKmRate);
        }
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
    
    public RiderProfile getRider() {
        return rider;
    }
    
    public void setRider(RiderProfile rider) {
        this.rider = rider;
    }
    
    public DriverProfile getDriver() {
        return driver;
    }
    
    public void setDriver(DriverProfile driver) {
        this.driver = driver;
    }
    
    public Location getPickupLocation() {
        return pickupLocation;
    }
    
    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
    
    public Location getDropoffLocation() {
        return dropoffLocation;
    }
    
    public void setDropoffLocation(Location dropoffLocation) {
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
        return "Trip{" +
                "id=" + id +
                ", state=" + state +
                ", riderId=" + (rider != null ? rider.getId() : null) +
                ", driverId=" + (driver != null ? driver.getId() : null) +
                ", requestedAt=" + requestedAt +
                '}';
    }
}
