package tech.oliver.promowisems.client.dto;

public record PartnerCouponStatusResponse(
        String couponCode,
        String status,
        int discountPercentage,
        String validUntil,
        String reason
) {}
