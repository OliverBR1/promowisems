package tech.oliver.promowisems.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {
    @Id
    private String couponCode;
    private int discountPercentage;
    private int remainingUses;
    private LocalDateTime validUntil;

    public Coupon() {}

    public Coupon(String couponCode, int discountPercentage, int remainingUses, LocalDateTime validUntil) {
        this.couponCode = couponCode;
        this.discountPercentage = discountPercentage;
        this.remainingUses = remainingUses;
        this.validUntil = validUntil;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public int getRemainingUses() {
        return remainingUses;
    }

    public void setRemainingUses(int remainingUses) {
        this.remainingUses = remainingUses;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isUsable() {
        return remainingUses > 0 && validUntil.isAfter(LocalDateTime.now());
    }
}
