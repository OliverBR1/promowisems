package tech.oliver.promowisems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.oliver.promowisems.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
}
