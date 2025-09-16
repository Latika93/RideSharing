package com.RideSharing.RideSharing.dto;

import com.RideSharing.RideSharing.entity.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO for creating/updating coupons
 */
public class CouponRequest {
    
    private String couponCode;
    private String description;
    private DiscountType discountType;
    private Double discountValue;
    private Double minimumFare;
    private Double maximumDiscount;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime validFrom;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime validUntil;
    
    private Integer usageLimit;
    private Boolean isActive = true;
    
    // Default constructor
    public CouponRequest() {}
    
    // Constructor with required fields
    public CouponRequest(String couponCode, String description, DiscountType discountType, 
                        Double discountValue, LocalDateTime validFrom, LocalDateTime validUntil) {
        this.couponCode = couponCode;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }
    
    // Getters and Setters
    public String getCouponCode() {
        return couponCode;
    }
    
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public DiscountType getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }
    
    public Double getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }
    
    public Double getMinimumFare() {
        return minimumFare;
    }
    
    public void setMinimumFare(Double minimumFare) {
        this.minimumFare = minimumFare;
    }
    
    public Double getMaximumDiscount() {
        return maximumDiscount;
    }
    
    public void setMaximumDiscount(Double maximumDiscount) {
        this.maximumDiscount = maximumDiscount;
    }
    
    public LocalDateTime getValidFrom() {
        return validFrom;
    }
    
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }
    
    public LocalDateTime getValidUntil() {
        return validUntil;
    }
    
    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
    
    public Integer getUsageLimit() {
        return usageLimit;
    }
    
    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    @Override
    public String toString() {
        return "CouponRequest{" +
                "couponCode='" + couponCode + '\'' +
                ", description='" + description + '\'' +
                ", discountType=" + discountType +
                ", discountValue=" + discountValue +
                ", minimumFare=" + minimumFare +
                ", maximumDiscount=" + maximumDiscount +
                ", validFrom=" + validFrom +
                ", validUntil=" + validUntil +
                ", usageLimit=" + usageLimit +
                ", isActive=" + isActive +
                '}';
    }
}

