package org.com.dianping.DTO;
import org.com.dianping.entity.Coupon;

public record CouponResponse(
        Long id,
        String couponName,
        Long userId,
        Integer couponAmount) {
    public CouponResponse(Coupon coupon) {
        this(coupon.getId(), coupon.getCouponName(), coupon.getUserId(), coupon.getCouponAmount());
    }
}