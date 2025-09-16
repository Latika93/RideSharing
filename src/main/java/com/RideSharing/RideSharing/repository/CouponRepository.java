package com.RideSharing.RideSharing.repository;

import com.RideSharing.RideSharing.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Coupon entity
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    /**
     * Find coupon by coupon code
     * @param couponCode The coupon code to search for
     * @return Optional containing the coupon if found
     */
    Optional<Coupon> findByCouponCode(String couponCode);
    
    /**
     * Find all active coupons
     * @return List of active coupons
     */
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true")
    List<Coupon> findAllActive();
    
    /**
     * Find all valid coupons (active and within validity period)
     * @param currentTime Current timestamp
     * @return List of valid coupons
     */
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true " +
           "AND c.validFrom <= :currentTime " +
           "AND c.validUntil >= :currentTime " +
           "AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)")
    List<Coupon> findAllValid(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find valid coupon by code
     * @param couponCode The coupon code to search for
     * @param currentTime Current timestamp
     * @return Optional containing the valid coupon if found
     */
    @Query("SELECT c FROM Coupon c WHERE c.couponCode = :couponCode " +
           "AND c.isActive = true " +
           "AND c.validFrom <= :currentTime " +
           "AND c.validUntil >= :currentTime " +
           "AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)")
    Optional<Coupon> findValidByCouponCode(@Param("couponCode") String couponCode, 
                                          @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find coupons by discount type
     * @param discountType The discount type to filter by
     * @return List of coupons with the specified discount type
     */
    List<Coupon> findByDiscountType(com.RideSharing.RideSharing.entity.DiscountType discountType);
    
    /**
     * Find coupons expiring soon
     * @param expiryDate The date to check for expiry
     * @return List of coupons expiring before the specified date
     */
    @Query("SELECT c FROM Coupon c WHERE c.validUntil <= :expiryDate AND c.isActive = true")
    List<Coupon> findExpiringSoon(@Param("expiryDate") LocalDateTime expiryDate);
    
    /**
     * Check if coupon code exists
     * @param couponCode The coupon code to check
     * @return true if the coupon code exists, false otherwise
     */
    boolean existsByCouponCode(String couponCode);
}

