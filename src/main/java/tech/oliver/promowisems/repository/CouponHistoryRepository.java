package tech.oliver.promowisems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.oliver.promowisems.entity.CouponHistory;

import java.util.List;

@Repository
public interface CouponHistoryRepository extends JpaRepository<CouponHistory, Long> {

    List<CouponHistory> findByCouponCodeOrderByValidatedAtDesc(String couponCode);
}
