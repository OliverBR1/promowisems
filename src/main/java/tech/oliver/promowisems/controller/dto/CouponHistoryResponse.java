package tech.oliver.promowisems.controller.dto;

import tech.oliver.promowisems.entity.CouponHistory;

import java.time.LocalDateTime;

public record CouponHistoryResponse(
        String couponCode,
        boolean valid,
        int discountPercentage,
        LocalDateTime validatedAt) {

    public static CouponHistoryResponse from(CouponHistory history) {
        return new CouponHistoryResponse(
                history.getCouponCode(),
                history.isValid(),
                history.getDiscountPercentage(),
                history.getValidatedAt()
        );
    }
}
