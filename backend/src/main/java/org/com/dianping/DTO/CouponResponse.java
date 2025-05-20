package org.com.dianping.DTO;
import org.com.dianping.entity.Coupon;

public record CouponResponse(
    String couponName,
    Long userId,
    Integer couponAmount,
    String category,
    String shop_id,
    String type,
    Double value,
    Double minAmount,
    Double maxAmount,
    String expireTime) {
    public CouponResponse(Coupon coupon) {
        this(coupon.getCouponName(), coupon.getUserId(), coupon.getCouponAmount(), coupon.getCategory(), coupon.getShopId().toString(), coupon.getType(), coupon.getValue(), coupon.getMinAmount(), coupon.getMaxAmount(), coupon.getExpireTime().toString());
    }
}