package org.com.dianping.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.com.dianping.entity.Coupon;
import org.com.dianping.entity.Merchant;
import org.com.dianping.entity.Order;
import org.com.dianping.entity.PackageGroup;
import org.com.dianping.repository.CouponRepository;
import org.com.dianping.repository.MerchantRepository;
import org.com.dianping.repository.OrderRepository;
import org.com.dianping.repository.PackageGroupRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

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

    public Order createOrder(Long userId, Long packageId) {
        // 获取套餐信息
        PackageGroup pkg = packageGroupRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("套餐不存在"));
        Merchant merchant= merchantRepository.findById(pkg.getMerchantId()) 
                .orElseThrow(() -> new RuntimeException("套餐不存在"));
    
        // 获取用户所有可用优惠券
        List<Coupon> validCoupons = couponRepository.findValidCouponsByUserId(userId);
        
        // 计算最终价格（使用最优优惠券）
        double finalPrice = calculateBestPrice(pkg.getPrice(), validCoupons);
    
        // 生成唯一券码
        String voucherCode = generateVoucherCode();
    
        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setPackageId(packageId);
        order.setCreateTime(LocalDateTime.now());
        order.setBusinessName(merchant.getMerchantName());
        order.setOriginalPrice(pkg.getPrice());
        order.setFinalPrice(finalPrice);
        order.setVoucherCode(voucherCode);
        order.setStatus("未使用");
    
        // 保存订单并更新销量
        orderRepository.save(order);
        packageGroupRepository.incrementSales(packageId);
    
        return order;
    }

    public Double calculateBestPrice(Double originalPrice, List<Coupon> coupon) {
        if (coupon == null) {
            return originalPrice;
        }
        if (originalPrice >= 100) {
            for (Coupon c : coupon) {
                if (c.getCouponName() == "满100减20") {
                    return originalPrice - 20;
                }
            }
            for (Coupon c : coupon) {
                if (c.getCouponName() == "满50减10") {
                    return originalPrice - 10;
                }
            }
            for (Coupon c : coupon) {
                if (c.getCouponName() == "满30减8") {
                    return originalPrice - 8;
                }
            }
            for (Coupon c : coupon) {
                if (c.getCouponName() == "无门槛优惠券") {
                    return originalPrice - 5;
                }
            }
            return originalPrice;
        }else if (originalPrice >= 50) {
            for (Coupon c : coupon) {
                if (c.getCouponName() == "满50减10") {
                    return originalPrice - 10;
                }
            }
            for (Coupon c : coupon) {
                if (c.getCouponName() == "满30减8") {
                    return originalPrice - 8;
                }
            }
            for (Coupon c : coupon) {
                if (c.getCouponName() == "无门槛优惠券") {
                    return originalPrice - 5;
                }
            }
            return originalPrice;
        }else if (originalPrice >= 30) {
            for (Coupon c : coupon) {
                if (c.getCouponName() == "满30减8") {
                    return originalPrice - 8;
                }
            }
            for (Coupon c : coupon) {
                if (c.getCouponName() == "无门槛优惠券") {
                    return originalPrice - 5;
                }
            } 
            return originalPrice;
        }else{
            for (Coupon c : coupon) {
                if (c.getCouponName() == "无门槛优惠券") {
                    return originalPrice - 5;
                }
            } 
            return originalPrice;
        }
    }

    private String generateVoucherCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 16).toUpperCase();
    }
}
