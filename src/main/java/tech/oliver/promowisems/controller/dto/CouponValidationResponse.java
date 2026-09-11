package tech.oliver.promowisems.controller.dto;

import tech.oliver.promowisems.entity.Coupon;

import java.time.LocalDateTime;

public record CouponValidationResponse(
        String couponCode,
        boolean valid,
        int discountPercentage,
        LocalDateTime validatedAt,
        int remainingUses,
        String message){

    public static CouponValidationResponse from(Coupon coupon, boolean valid, String message) {
        return new CouponValidationResponse(
                coupon.getCouponCode(),
                valid,
                valid ? coupon.getDiscountPercentage() : 0,
                LocalDateTime.now(),
                coupon.getRemainingUses(),
                message
        );
    }
}
