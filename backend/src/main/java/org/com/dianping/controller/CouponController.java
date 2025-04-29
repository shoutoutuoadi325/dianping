package org.com.dianping.controller;

import java.util.List;

import org.com.dianping.entity.Coupon;
import org.com.dianping.service.CouponService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public List<Coupon> getUserCoupons(@RequestHeader("UserId") Long userId) {
        return couponService.getValidCouponsForUser(userId);
    }
    
    // 可以添加更多优惠券相关接口
}