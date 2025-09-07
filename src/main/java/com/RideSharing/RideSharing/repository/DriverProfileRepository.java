package com.RideSharing.RideSharing.repository;

import com.RideSharing.RideSharing.entity.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
}
