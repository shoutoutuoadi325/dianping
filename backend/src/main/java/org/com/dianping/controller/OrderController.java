package org.com.dianping.controller;
import org.com.dianping.DTO.OrderRequest;
import org.com.dianping.entity.Coupon;
import org.com.dianping.entity.Order;
import org.com.dianping.service.CouponService;
import org.com.dianping.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final CouponService couponService;

    public OrderController(OrderService orderService, CouponService couponService) {
        this.orderService = orderService;
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader("UserId") Long userId,
            @RequestBody OrderRequest request
    ) {
        try {
            Order order = orderService.createOrder(
                    userId,
                    request.getPackageId(),
                //TODO coupon implement
                    request.getCouponId()
            );
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/coupons")
    public List<Coupon> getValidCoupons(@RequestHeader("UserId") Long userId) {
        return couponService.getValidCouponsForUser(userId);
    }
}
