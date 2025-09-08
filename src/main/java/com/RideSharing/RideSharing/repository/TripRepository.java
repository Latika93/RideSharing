package com.RideSharing.RideSharing.repository;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.RiderProfile;
import com.RideSharing.RideSharing.entity.Trip;
import com.RideSharing.RideSharing.entity.TripState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Trip entity operations
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    /**
     * Find trips by rider
     */
    List<Trip> findByRiderOrderByRequestedAtDesc(RiderProfile rider);
    
    /**
     * Find trips by driver
     */
    List<Trip> findByDriverOrderByRequestedAtDesc(DriverProfile driver);
    
    /**
     * Find trips by state
     */
    List<Trip> findByState(TripState state);
    
    /**
     * Find active trips for a rider (REQUESTED, ACCEPTED, STARTED)
     */
    @Query("SELECT t FROM Trip t WHERE t.rider = :rider AND t.state IN ('REQUESTED', 'ACCEPTED', 'STARTED')")
    List<Trip> findActiveTripsByRider(@Param("rider") RiderProfile rider);
    
    /**
     * Find active trips for a driver (ACCEPTED, STARTED)
     */
    @Query("SELECT t FROM Trip t WHERE t.driver = :driver AND t.state IN ('ACCEPTED', 'STARTED')")
    List<Trip> findActiveTripsByDriver(@Param("driver") DriverProfile driver);
    
    /**
     * Find trips by rider and state
     */
    List<Trip> findByRiderAndState(RiderProfile rider, TripState state);
    
    /**
     * Find trips by driver and state
     */
    List<Trip> findByDriverAndState(DriverProfile driver, TripState state);
    
    /**
     * Find the latest active trip for a rider
     */
    @Query("SELECT t FROM Trip t WHERE t.rider = :rider AND t.state IN ('REQUESTED', 'ACCEPTED', 'STARTED') ORDER BY t.requestedAt DESC")
    Optional<Trip> findLatestActiveTripByRider(@Param("rider") RiderProfile rider);
    
    /**
     * Find the latest active trip for a driver
     */
    @Query("SELECT t FROM Trip t WHERE t.driver = :driver AND t.state IN ('ACCEPTED', 'STARTED') ORDER BY t.acceptedAt DESC")
    Optional<Trip> findLatestActiveTripByDriver(@Param("driver") DriverProfile driver);
    
    /**
     * Find trips created within a time range
     */
    List<Trip> findByRequestedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find completed trips for a rider
     */
    @Query("SELECT t FROM Trip t WHERE t.rider = :rider AND t.state = 'COMPLETED' ORDER BY t.completedAt DESC")
    List<Trip> findCompletedTripsByRider(@Param("rider") RiderProfile rider);
    
    /**
     * Find completed trips for a driver
     */
    @Query("SELECT t FROM Trip t WHERE t.driver = :driver AND t.state = 'COMPLETED' ORDER BY t.completedAt DESC")
    List<Trip> findCompletedTripsByDriver(@Param("driver") DriverProfile driver);
    
    /**
     * Count trips by state
     */
    long countByState(TripState state);
    
    /**
     * Count trips by rider and state
     */
    long countByRiderAndState(RiderProfile rider, TripState state);
    
    /**
     * Count trips by driver and state
     */
    long countByDriverAndState(DriverProfile driver, TripState state);
}
