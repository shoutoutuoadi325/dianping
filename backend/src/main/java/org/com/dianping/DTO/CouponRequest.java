package org.com.dianping.DTO;

public record CouponRequest(
    String couponName,
    Long userId,
    Integer couponAmount
) {
}