package org.com.dianping.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.com.dianping.DTO.OrderResponse;
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

    @Transactional
    public Order createOrder(Long userId, Long packageId, String MerchantCategory, Long merchantId) {
        // 获取套餐信息
        PackageGroup pkg = packageGroupRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("套餐不存在"));
        Merchant merchant = merchantRepository.findById(pkg.getMerchantId())
                .orElseThrow(() -> new RuntimeException("套餐不存在"));

        // 获取用户所有可用优惠券
        List<Coupon> validCoupons = couponRepository.findValidCouponsByUserIdAndMerchant(userId, MerchantCategory, merchantId, pkg.getPrice());

        // 计算最终价格（使用最优优惠券）
        UsedCoupon couponUsing = calculateBestPrice(pkg.getPrice(), validCoupons);
        Double finalPrice = pkg.getPrice() - couponUsing.Discount;
        Coupon bestCoupon = couponUsing.coupon;

        // 生成唯一券码
        String voucherCode = generateVoucherCode();

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setPackageId(packageId);
        order.setCreateTime(LocalDateTime.now());
        order.setBusinessName(merchant.getMerchantName());
        order.setOriginalPrice(pkg.getPrice());
        order.setBestCoupon(bestCoupon);
        order.setFinalPrice(finalPrice);
        order.setVoucherCode(voucherCode);
        order.setStatus("未使用");

        // 保存订单并更新销量
        orderRepository.save(order);
        packageGroupRepository.incrementSales(packageId);

        return order;
    }

    public OrderResponse getOrderDetails(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!(order.getUserId()==userId)) {
            throw new RuntimeException("无权查看该订单");
        }
        PackageGroup pkg = packageGroupRepository.findById(order.getPackageId())
                .orElseThrow(() -> new RuntimeException("套餐不存在"));
        return new OrderResponse(order, pkg);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(order -> {
            PackageGroup pkg = packageGroupRepository.findById(order.getPackageId())
                    .orElseThrow(() -> new RuntimeException("套餐不存在"));
            return new OrderResponse(order, pkg);
        }).collect(Collectors.toList());
    }

    public boolean checkUserOrders(Long userId) {
        return orderRepository.existsByUserId(userId);
    }

    

    public UsedCoupon calculateBestPrice(Double originalPrice, List<Coupon> coupons) {
        if (coupons.isEmpty()) {
            return new UsedCoupon();
        }

        double discount = 0.0;
        Coupon bestCoupon = null;
        for (Coupon coupon : coupons) {
            if(computeDiscont(originalPrice, coupon)>discount) {
                discount = computeDiscont(originalPrice, coupon);
                bestCoupon = coupon;
            }
        }
        UsedCoupon couponUsing = new UsedCoupon(bestCoupon, discount);
        return couponUsing;
    }
    
    public Double computeDiscont(Double originalPrice, Coupon coupon) {
        double discount = 0.0;
        switch (coupon.getType()) {
            case "满减":
                discount = coupon.getValue();
                break;
            case "折扣":
                discount = originalPrice*(coupon.getValue()/10);
                break;
            case "立减":
                discount = Math.min(coupon.getValue(), originalPrice);
                break;
            case "免单":
                discount = originalPrice;
                break;
            default:
                throw new IllegalArgumentException("无效的优惠券类型");
        }
        return discount;
    }
    
    // 新增内部类定义优惠券规则
    private static class CouponRule {
        final Predicate<Coupon> condition;
        final Function<Coupon, Double> discount;
    
        CouponRule(Predicate<Coupon> condition, Function<Coupon, Double> discount) {
            this.condition = condition;
            this.discount = discount;
        }
    }

    private String generateVoucherCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 16).toUpperCase();
    }
}

class UsedCoupon {
    public Coupon coupon;
    public Double Discount;

    public UsedCoupon() {
        this.coupon = null;
        this.Discount = 0.0;
    }

    public UsedCoupon(Coupon coupon, Double Discount) {
        this.coupon = coupon;
        this.Discount = Discount;
    }
}