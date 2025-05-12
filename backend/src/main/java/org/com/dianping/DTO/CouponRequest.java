package org.com.dianping.DTO;

public record CouponRequest(
    String couponName,
    Long userId,
    Integer couponAmount,
    String category,
    String shop_id,
    String type,
    Integer value,
    Integer miniAmount,
    String expireTime
) {
}