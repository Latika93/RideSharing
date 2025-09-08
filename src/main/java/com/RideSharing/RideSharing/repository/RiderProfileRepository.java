package com.RideSharing.RideSharing.repository;

import com.RideSharing.RideSharing.entity.RiderProfile;
import com.RideSharing.RideSharing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderProfileRepository extends JpaRepository<RiderProfile, Long> {
    RiderProfile findByUser(User user);
}
