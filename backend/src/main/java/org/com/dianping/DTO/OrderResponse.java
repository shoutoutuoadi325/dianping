package org.com.dianping.DTO;

import org.com.dianping.entity.Order;
import org.com.dianping.entity.PackageGroup;

public record OrderResponse(
        Long orderId,
        String couponCode,
        String orderNo,
        String packageTitle,
        String businessName
) {
    public OrderResponse(Order order, PackageGroup pkg) {
        this(order.getId(), order.getVoucherCode(), "ORDER_" + order.getId(),
                pkg.getTitle(), order.getBusinessName());
    }
}