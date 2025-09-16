package com.RideSharing.RideSharing.service;

import com.RideSharing.RideSharing.context.FareCalculatorContext;
import com.RideSharing.RideSharing.dto.CouponRequest;
import com.RideSharing.RideSharing.dto.FareCalculationRequest;
import com.RideSharing.RideSharing.dto.FareCalculationResult;
import com.RideSharing.RideSharing.entity.Coupon;
import com.RideSharing.RideSharing.entity.DiscountType;
import com.RideSharing.RideSharing.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for fare calculation and coupon management
 */
@Service
@Transactional
public class FareCalculationService {
    
    @Autowired
    private FareCalculatorContext fareCalculatorContext;
    
    @Autowired
    private CouponRepository couponRepository;
    
    /**
     * Calculate fare for a ride request
     * @param request The fare calculation request
     * @return The calculated fare result
     */
    public FareCalculationResult calculateFare(FareCalculationRequest request) {
        // Validate request
        validateFareCalculationRequest(request);
        
        // Calculate base fare using appropriate strategy
        FareCalculationResult result = fareCalculatorContext.calculateFare(request);
        
        // Apply coupon discount if provided
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            applyCouponDiscount(result, request.getCouponCode());
        }
        
        return result;
    }
    
    /**
     * Create a new coupon
     * @param couponRequest The coupon creation request
     * @return The created coupon
     */
    public Coupon createCoupon(CouponRequest couponRequest) {
        // Validate coupon request
        validateCouponRequest(couponRequest);
        
        // Check if coupon code already exists
        if (couponRepository.existsByCouponCode(couponRequest.getCouponCode())) {
            throw new IllegalArgumentException("Coupon code already exists: " + couponRequest.getCouponCode());
        }
        
        // Create coupon entity
        Coupon coupon = new Coupon();
        coupon.setCouponCode(couponRequest.getCouponCode());
        coupon.setDescription(couponRequest.getDescription());
        coupon.setDiscountType(couponRequest.getDiscountType());
        coupon.setDiscountValue(couponRequest.getDiscountValue());
        coupon.setMinimumFare(couponRequest.getMinimumFare());
        coupon.setMaximumDiscount(couponRequest.getMaximumDiscount());
        coupon.setValidFrom(couponRequest.getValidFrom());
        coupon.setValidUntil(couponRequest.getValidUntil());
        coupon.setUsageLimit(couponRequest.getUsageLimit());
        coupon.setIsActive(couponRequest.getIsActive());
        
        return couponRepository.save(coupon);
    }
    
    /**
     * Get all active coupons
     * @return List of active coupons
     */
    @Transactional(readOnly = true)
    public List<Coupon> getAllActiveCoupons() {
        return couponRepository.findAllActive();
    }
    
    /**
     * Get all valid coupons
     * @return List of valid coupons
     */
    @Transactional(readOnly = true)
    public List<Coupon> getAllValidCoupons() {
        return couponRepository.findAllValid(LocalDateTime.now());
    }
    
    /**
     * Get coupon by code
     * @param couponCode The coupon code
     * @return Optional containing the coupon if found
     */
    @Transactional(readOnly = true)
    public Optional<Coupon> getCouponByCode(String couponCode) {
        return couponRepository.findByCouponCode(couponCode);
    }
    
    /**
     * Update coupon
     * @param id The coupon ID
     * @param couponRequest The update request
     * @return The updated coupon
     */
    public Coupon updateCoupon(Long id, CouponRequest couponRequest) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found with ID: " + id));
        
        // Update fields
        coupon.setDescription(couponRequest.getDescription());
        coupon.setDiscountType(couponRequest.getDiscountType());
        coupon.setDiscountValue(couponRequest.getDiscountValue());
        coupon.setMinimumFare(couponRequest.getMinimumFare());
        coupon.setMaximumDiscount(couponRequest.getMaximumDiscount());
        coupon.setValidFrom(couponRequest.getValidFrom());
        coupon.setValidUntil(couponRequest.getValidUntil());
        coupon.setUsageLimit(couponRequest.getUsageLimit());
        coupon.setIsActive(couponRequest.getIsActive());
        coupon.setUpdatedAt(LocalDateTime.now());
        
        return couponRepository.save(coupon);
    }
    
    /**
     * Delete coupon
     * @param id The coupon ID
     */
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new IllegalArgumentException("Coupon not found with ID: " + id);
        }
        couponRepository.deleteById(id);
    }
    
    /**
     * Apply coupon discount to fare calculation result
     * @param result The fare calculation result
     * @param couponCode The coupon code to apply
     */
    private void applyCouponDiscount(FareCalculationResult result, String couponCode) {
        Optional<Coupon> couponOpt = couponRepository.findValidByCouponCode(couponCode, LocalDateTime.now());
        
        if (couponOpt.isEmpty()) {
            // Coupon not found or invalid, no discount applied
            return;
        }
        
        Coupon coupon = couponOpt.get();
        
        // Check minimum fare requirement
        if (coupon.getMinimumFare() != null && result.getSubtotal() < coupon.getMinimumFare()) {
            return; // Minimum fare not met
        }
        
        double discountAmount = calculateDiscountAmount(result.getSubtotal(), coupon);
        
        // Apply maximum discount limit if specified
        if (coupon.getMaximumDiscount() != null && discountAmount > coupon.getMaximumDiscount()) {
            discountAmount = coupon.getMaximumDiscount();
        }
        
        // Apply discount
        result.setDiscountAmount(discountAmount);
        result.setAppliedCouponCode(couponCode);
        result.setFinalFare(result.getSubtotal() - discountAmount);
        
        // Ensure final fare is not negative
        if (result.getFinalFare() < 0) {
            result.setFinalFare(0.0);
        }
    }
    
    /**
     * Calculate discount amount based on coupon type
     * @param subtotal The fare subtotal
     * @param coupon The coupon to apply
     * @return The discount amount
     */
    private double calculateDiscountAmount(double subtotal, Coupon coupon) {
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            return subtotal * (coupon.getDiscountValue() / 100.0);
        } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return coupon.getDiscountValue();
        }
        return 0.0;
    }
    
    /**
     * Validate fare calculation request
     * @param request The request to validate
     */
    private void validateFareCalculationRequest(FareCalculationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Fare calculation request cannot be null");
        }
        
        if (request.getDistance() == null || request.getDistance() <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }
        
        if (request.getDuration() == null || request.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        
        if (request.getBaseRate() == null || request.getBaseRate() <= 0) {
            throw new IllegalArgumentException("Base rate must be greater than 0");
        }
    }
    
    /**
     * Validate coupon request
     * @param request The request to validate
     */
    private void validateCouponRequest(CouponRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Coupon request cannot be null");
        }
        
        if (request.getCouponCode() == null || request.getCouponCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code is required");
        }
        
        if (request.getDiscountType() == null) {
            throw new IllegalArgumentException("Discount type is required");
        }
        
        if (request.getDiscountValue() == null || request.getDiscountValue() <= 0) {
            throw new IllegalArgumentException("Discount value must be greater than 0");
        }
        
        if (request.getValidFrom() == null || request.getValidUntil() == null) {
            throw new IllegalArgumentException("Valid from and valid until dates are required");
        }
        
        if (request.getValidFrom().isAfter(request.getValidUntil())) {
            throw new IllegalArgumentException("Valid from date must be before valid until date");
        }
        
        if (request.getDiscountType() == DiscountType.PERCENTAGE && request.getDiscountValue() > 100) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
        }
    }
}
