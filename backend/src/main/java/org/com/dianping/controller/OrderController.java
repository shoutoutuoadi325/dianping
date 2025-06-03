package org.com.dianping.controller;

import java.util.HashMap;
import java.util.Map;

import org.com.dianping.entity.Order;
import org.com.dianping.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader("UserId") Long userId,
            @RequestBody Map<String, Object> orderRequest) {
        try {
            // 从请求体中获取必要的参数
            Long packageId = Long.valueOf(orderRequest.get("packageId").toString());
            Long merchantId = Long.valueOf(orderRequest.get("businessId").toString());
            String invitationCode = orderRequest.get("invitationCode") != null ? orderRequest.get("invitationCode").toString() : null;

            // 创建订单
            Order savedOrder = orderService.createOrder(userId, packageId, merchantId,invitationCode);

            // 构造返回结果
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", savedOrder.getId());
            response.put("message", "订单创建成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 打印错误堆栈便于调试
            Map<String, String> error = new HashMap<>();
            error.put("message", "创建订单失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/with-invitation-code")
    public ResponseEntity<?> createOrderWithInvitationCode(
            @RequestHeader("UserId") Long userId,
            @RequestBody Map<String, Object> orderRequest) {
        try {
            // 从请求体中获取必要的参数
            Long packageId = Long.valueOf(orderRequest.get("packageId").toString());
            Long merchantId = Long.valueOf(orderRequest.get("businessId").toString());
            String invitationCode = orderRequest.get("invitationCode") != null ? orderRequest.get("invitationCode").toString() : null;

            // 创建订单
            Order savedOrder = orderService.createOrder(userId, packageId, merchantId, invitationCode);

            // 构造返回结果
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", savedOrder.getId());
            response.put("message", "订单创建成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 打印错误堆栈便于调试
            Map<String, String> error = new HashMap<>();
            error.put("message", "创建订单失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @RequestHeader("UserId") Long userId,
            @PathVariable Long orderId
    ) {
        try {
            return ResponseEntity.ok(orderService.getOrderDetails(userId, orderId));
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取订单详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserOrders(@RequestHeader("UserId") Long userId) {
        try {
            return ResponseEntity.ok(orderService.getUserOrders(userId));
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取订单列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/check-user-orders")
    public ResponseEntity<Boolean> checkUserOrders(@RequestHeader("UserId") Long userId) {
        boolean hasOrders = orderService.checkUserOrders(userId);
        return ResponseEntity.ok(hasOrders);
    }
}