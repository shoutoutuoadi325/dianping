package org.com.dianping.repository;

import org.com.dianping.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // 根据ID和用户ID查询优惠券
    Optional<Coupon> findByIdAndUserId(Long couponId, Long userId);
    
    // 查询用户所有有效优惠券(amount > 0)
    @Query("SELECT c FROM Coupon c WHERE c.userId = :userId AND c.couponAmount > 0")
    List<Coupon> findValidCouponsByUserId(@Param("userId") Long userId);
    
    // 查询用户所有优惠券的基本信息
    @Query("SELECT c.id, c.couponName, c.userId, c.couponAmount FROM Coupon c WHERE c.userId = :userId")
    List<Object[]> findCouponBasicInfoByUserId(@Param("userId") Long userId);
}
