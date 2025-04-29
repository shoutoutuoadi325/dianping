package org.com.dianping.controller;

import java.util.Map;

import org.com.dianping.DTO.OrderRequest;
import org.com.dianping.entity.Order;
import org.com.dianping.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单API控制器
 * <p>
 * 处理订单创建相关请求
 * </p>
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建新订单
     * @param userId 用户ID(请求头)
     * @param request 订单请求体
     * @return 创建成功的订单或错误信息
     */
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader("UserId") Long userId,
            @RequestBody OrderRequest request
    ) {
        try {
            Order order = orderService.createOrder(
                    userId,
                    request.getPackageId()
            );
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "订单创建失败",
                            "message", e.getMessage()
                    ));
        }
    }
}
