package tech.oliver.promowisems.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_history")
public class CouponHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String couponCode;
    private boolean valid;
    private int discountPercentage;
    private LocalDateTime validatedAt;

    public CouponHistory() {}

    public CouponHistory(String couponCode, boolean valid, int discountPercentage, LocalDateTime validatedAt) {
        this.couponCode = couponCode;
        this.valid = valid;
        this.discountPercentage = discountPercentage;
        this.validatedAt = validatedAt;
    }

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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public static CouponHistory from(Coupon coupon, boolean valid) {
        return new CouponHistory(
                coupon.getCouponCode(),
                valid,
                valid ? coupon.getDiscountPercentage() : 0,
                LocalDateTime.now()
        );
    }
}
