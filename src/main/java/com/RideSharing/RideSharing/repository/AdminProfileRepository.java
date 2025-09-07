package com.RideSharing.RideSharing.repository;

import com.RideSharing.RideSharing.entity.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {
}
