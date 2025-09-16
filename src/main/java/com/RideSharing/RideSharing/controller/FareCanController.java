package com.RideSharing.RideSharing.controller;

import com.RideSharing.RideSharing.dto.CouponRequest;
import com.RideSharing.RideSharing.dto.FareCalculationRequest;
import com.RideSharing.RideSharing.dto.FareCalculationResult;
import com.RideSharing.RideSharing.entity.Coupon;
import com.RideSharing.RideSharing.service.FareCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth/fare")
public class FareCanController {

    @Autowired
    private FareCalculationService fareCalculationService;

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateFare(@RequestBody FareCalculationRequest request) {
        try {
            FareCalculationResult result = fareCalculationService.calculateFare(request);
            System.out.println("result : " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }

//        return ResponseEntity.ok(response);
    }

    @PostMapping("/coupons")
    public ResponseEntity<?> createCoupon(@RequestBody CouponRequest request) {
        try {
            Coupon coupon = fareCalculationService.createCoupon(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(coupon);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid request");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("message", "An unexpected error occurred while creating coupon");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get all valid coupons
     * GET /api/fare/coupons/valid
     */
    @GetMapping("/coupons/valid")
    public ResponseEntity<?> getAllValidCoupons() {
        try {
            List<Coupon> coupons = fareCalculationService.getAllValidCoupons();
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", "An unexpected error occurred while fetching valid coupons");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get coupon by code
     * GET /api/fare/coupons/{couponCode}
     */
    @GetMapping("/coupons/{couponCode}")
    public ResponseEntity<?> getCouponByCode(@PathVariable String couponCode) {
        try {
            Optional<Coupon> coupon = fareCalculationService.getCouponByCode(couponCode);
            if (coupon.isPresent()) {
                return ResponseEntity.ok(coupon.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Not found");
                error.put("message", "Coupon not found with code: " + couponCode);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", "An unexpected error occurred while fetching coupon");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Update coupon
     * PUT /api/fare/coupons/{id}
     */
    @PutMapping("/coupons/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody CouponRequest request) {
        try {
            Coupon coupon = fareCalculationService.updateCoupon(id, request);
            return ResponseEntity.ok(coupon);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid request");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", "An unexpected error occurred while updating coupon");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Delete coupon
     * DELETE /api/fare/coupons/{id}
     */
    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        try {
            fareCalculationService.deleteCoupon(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Coupon deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not found");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", "An unexpected error occurred while deleting coupon");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Health check endpoint
     * GET /api/fare/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Fare Calculation Service");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
