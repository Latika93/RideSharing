package com.RideSharing.RideSharing.repository;

import com.RideSharing.RideSharing.entity.DriverProfile;
import com.RideSharing.RideSharing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    DriverProfile findByUser(User user);
}
