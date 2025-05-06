package org.com.dianping.controller;

import java.util.List;

import org.com.dianping.entity.Coupon;
import org.com.dianping.service.CouponService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public List<Coupon> getUserCoupons(
        @RequestHeader("UserId") Long userId,
        @RequestParam(required = false) String merchantCategory) {
        return couponService.getValidCouponsForUser(userId, merchantCategory);
    }
    
    @PostMapping("/issue-by-choice")
    public void issueCouponByChoice(@RequestHeader("UserId") Long userId, @RequestParam char choice) {
        couponService.issueNewUserCoupons(userId, choice);
    }
    
    @PostMapping("/issue-by-name")
    public void issueCouponByName(@RequestHeader("UserId") Long userId, @RequestBody Coupon coupon) {
        coupon.setUserId(userId);
        couponService.saveCoupon(coupon);
    }
    
    @PostMapping("/use/{couponId}")
    public void useCoupon(@PathVariable Long couponId) {
        couponService.useCoupon(couponId);
    }
}