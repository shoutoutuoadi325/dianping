package org.com.dianping.service;
import jakarta.transaction.Transactional;
import org.com.dianping.entity.Coupon;
import org.com.dianping.entity.Merchant;
import org.com.dianping.entity.Order;
import org.com.dianping.entity.PackageGroup;
import org.com.dianping.repository.CouponRepository;
import org.com.dianping.repository.MerchantRepository;
import org.com.dianping.repository.OrderRepository;
import org.com.dianping.repository.PackageGroupRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final PackageGroupRepository packageGroupRepository;
    private final CouponRepository couponRepository;
    private final MerchantRepository merchantRepository;

    public OrderService(OrderRepository orderRepository,
                        PackageGroupRepository packageGroupRepository,
                        CouponRepository couponRepository, MerchantRepository merchantRepository) {
        this.orderRepository = orderRepository;
        this.packageGroupRepository = packageGroupRepository;
        this.couponRepository = couponRepository;
        this.merchantRepository = merchantRepository;
    }

    public Order createOrder(Long userId, Long packageId, Long couponId) {
        // 获取套餐信息
        PackageGroup pkg = packageGroupRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("套餐不存在"));
        Merchant merchant= merchantRepository.findById(pkg.getMerchantId()) .orElseThrow(() -> new RuntimeException("套餐不存在"));

        // 处理优惠券
        Coupon coupon = null;
        if (couponId != null) {
            coupon = (Coupon) couponRepository.findByIdAndUserId(couponId, userId)
                    .orElseThrow(() -> new RuntimeException("无效的优惠券"));
        }

        // 计算最终价格
        double finalPrice = calculateFinalPrice(pkg.getPrice(), coupon);

        // 生成唯一券码
        String voucherCode = generateVoucherCode();

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setPackageId(packageId);
        order.setCreateTime(LocalDateTime.now());
        order.setBusinessName(merchant.getMerchantName());
        order.setCouponId(couponId);
        order.setOriginalPrice(pkg.getPrice());
        order.setFinalPrice(finalPrice);
        order.setVoucherCode(voucherCode);
        order.setStatus("未使用");

        // 保存订单并更新销量
        orderRepository.save(order);
        packageGroupRepository.incrementSales(packageId);

        return order;
    }

    private double calculateFinalPrice(double originalPrice, Coupon coupon) {
        //TODO
//        if (coupon == null) return originalPrice;
//
//        return switch (coupon.getType()) {
//            case "FIXED" -> Math.max(originalPrice - coupon.getAmount(), 0);
//            case "PERCENT" -> originalPrice * (1 - coupon.getAmount() / 100);
//            default -> originalPrice;
//        };
        return originalPrice;
    }

    private String generateVoucherCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 16).toUpperCase();
    }
}
