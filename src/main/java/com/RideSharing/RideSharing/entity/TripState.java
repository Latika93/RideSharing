package com.RideSharing.RideSharing.entity;

/**
 * Enum representing the possible states of a trip in the FSM
 */
public enum TripState {
    REQUESTED,  // Rider has requested a trip
    ACCEPTED,   // Driver has accepted the trip
    STARTED,    // Trip has started (rider is onboard)
    COMPLETED,  // Trip has been completed
    CANCELLED   // Trip has been cancelled
}
