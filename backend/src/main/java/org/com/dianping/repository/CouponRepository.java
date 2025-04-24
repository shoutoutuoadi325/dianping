package org.com.dianping.repository;

import org.com.dianping.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Object> findByIdAndUserId(Long couponId, Long userId);
    //TODO
}
