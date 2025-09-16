package com.RideSharing.RideSharing.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity representing a coupon for fare discounts
 */
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String couponCode;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private Double discountValue;

    @Column(name = "minimum_fare")
    private Double minimumFare;

    @Column(name = "maximum_discount")
    private Double maximumDiscount;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public Coupon() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public Coupon(String couponCode, String description, DiscountType discountType,
                  Double discountValue, LocalDateTime validFrom, LocalDateTime validUntil) {
        this();
        this.couponCode = couponCode;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Check if the coupon is valid for use
     *
     * @return true if the coupon is valid, false otherwise
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive &&
                now.isAfter(validFrom) &&
                now.isBefore(validUntil) &&
                (usageLimit == null || usedCount < usageLimit);
    }

    /**
     * Increment the usage count
     */
    public void incrementUsage() {
        this.usedCount++;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", couponCode='" + couponCode + '\'' +
                ", description='" + description + '\'' +
                ", discountType=" + discountType +
                ", discountValue=" + discountValue +
                ", minimumFare=" + minimumFare +
                ", maximumDiscount=" + maximumDiscount +
                ", validFrom=" + validFrom +
                ", validUntil=" + validUntil +
                ", usageLimit=" + usageLimit +
                ", usedCount=" + usedCount +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

