package org.com.dianping.controller;

import java.util.List;
import java.util.Map;

import org.com.dianping.DTO.OrderRequest;
import org.com.dianping.DTO.OrderResponse;
import org.com.dianping.entity.Order;
import org.com.dianping.service.OrderService;
import org.com.dianping.service.MerchantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final MerchantService merchantService;

    public OrderController(OrderService orderService, MerchantService merchantService) {
        this.orderService = orderService;
        this.merchantService = merchantService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader("UserId") Long userId,
            @RequestBody OrderRequest request
    ) {
        try {
            String merchantCategory = merchantService.getMerchantCategory(request.getBusinessId());
            Order order = orderService.createOrder(userId, request.getPackageId(), merchantCategory, request.getBusinessId());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "订单创建失败",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(
            @RequestHeader("UserId") Long userId,
            @PathVariable Long orderId
    ) {
        try {
            OrderResponse orderResponse = orderService.getOrderDetails(userId, orderId);
            return ResponseEntity.ok(orderResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@RequestHeader("UserId") Long userId) {
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/check-user-orders")
    public ResponseEntity<Boolean> checkUserOrders(@RequestHeader("UserId") Long userId) {
        boolean hasOrders = orderService.checkUserOrders(userId);
        return ResponseEntity.ok(hasOrders);
    }
}