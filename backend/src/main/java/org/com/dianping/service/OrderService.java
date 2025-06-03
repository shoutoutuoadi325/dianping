package org.com.dianping.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.com.dianping.DTO.OrderResponse;
import org.com.dianping.entity.Coupon;
import org.com.dianping.entity.Merchant;
import org.com.dianping.entity.Order;
import org.com.dianping.entity.PackageGroup;
import org.com.dianping.entity.User;
import org.com.dianping.repository.CouponRepository;
import org.com.dianping.repository.MerchantRepository;
import org.com.dianping.repository.OrderRepository;
import org.com.dianping.repository.PackageGroupRepository;
import org.com.dianping.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;   
@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final PackageGroupRepository packageGroupRepository;
    private final CouponRepository couponRepository;
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;
    private final InvitationService invitationService;  // 添加依赖

    public OrderService(OrderRepository orderRepository,
                        PackageGroupRepository packageGroupRepository,
                        CouponRepository couponRepository, 
                        MerchantRepository merchantRepository, 
                        UserRepository userRepository,
                        InvitationService invitationService) {  // 修改构造函数
        this.orderRepository = orderRepository;
        this.packageGroupRepository = packageGroupRepository;
        this.couponRepository = couponRepository;
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
        this.invitationService = invitationService;
    }

    @Transactional
    public Order createOrder(Long userId, Long packageId, Long merchantId, String invitationCode) {
        // 获取套餐信息和商家信息
        PackageGroup pkg = packageGroupRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("套餐不存在"));
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        // 获取可用优惠券
        List<Coupon> validCoupons = couponRepository.findValidCouponsByUserIdAndMerchant(
            userId, 
            merchant.getCategory(),
            merchant.getId(), 
            pkg.getPrice()
        );

        // 过滤和计算最优优惠券
        List<Coupon> usableCoupons = validCoupons.stream()
                .filter(coupon -> isCouponApplicable(coupon, merchant.getCategory()))
                .collect(Collectors.toList());

        UsedCoupon couponUsing = calculateBestPrice(pkg.getPrice(), usableCoupons);
        
        // 创建新订单
        Order order = new Order();
        order.setUserId(userId);
        order.setPackageId(packageId);
        order.setCreateTime(LocalDateTime.now());
        order.setBusinessName(merchant.getMerchantName());
        order.setOriginalPrice(pkg.getPrice());
        if(couponUsing.coupon!=null) {
            order.setBestCoupon(couponUsing.coupon.getId());
        }else{
            order.setBestCoupon(null);
        }

        order.setFinalPrice(Math.max(pkg.getPrice() - couponUsing.Discount, 0));
        order.setVoucherCode(generateVoucherCode());
        order.setStatus("未使用");

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 更新套餐销量
        packageGroupRepository.incrementSales(packageId);

        // 处理优惠券使用
        if (couponUsing.coupon != null) {
            CouponService couponService = new CouponService(couponRepository, userRepository);
            couponService.useCoupon(couponUsing.coupon.getId());
        }

        // 处理邀请记录 - 如果是被邀请的用户下单
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getInviterId() != null) {
            // 使用invitationService处理邀请记录和奖励
            invitationService.processInvitationReward(
                user.getInviterId(),  // 邀请人ID
                userId,               // 被邀请人ID
                savedOrder.getFinalPrice()  // 订单实付金额
            );
        }

        // 处理邀请码
        if (invitationCode != null && !invitationCode.isEmpty()) {
            // 检查是否是自己的邀请码
            User inviter = userRepository.findByInvitationCode(invitationCode)
                    .orElseThrow(() -> new RuntimeException("无效的邀请码"));
            if (inviter.getId().equals(userId)) {
                throw new RuntimeException("不能使用自己的邀请码");
            }

            // 检查是否已经使用过任何邀请码
            if (user.getInviterId() != null) {
                throw new RuntimeException("已经使用过邀请码");
            }

            // 设置邀请关系（无论订单金额是否满足要求，都记录邀请关系）
            user.setInviterId(inviter.getId());
            userRepository.save(user);
            
            // 只有订单金额大于10元时才记录邀请记录并处理奖励
            if (savedOrder.getFinalPrice() > 10.0) {
                invitationService.processInvitationReward(
                    inviter.getId(),
                    userId,
                    savedOrder.getFinalPrice()
                );
            }
        }

        return savedOrder;
    }

    private boolean isCouponApplicable(Coupon coupon, String merchantCategory) {
        // 如果优惠券没有指定品类限制，则可用于所有品类
        if (coupon.getCategory() == null) {
            return true;
        }
        // 优惠券指定了品类，则必须与商家品类匹配
        return coupon.getCategory().equals(merchantCategory);
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
        // 检查是否满足最低消费要求
        if (coupon.getMinAmount() != null && originalPrice < coupon.getMinAmount()) {
            return 0.0;
        }

        double discount = 0.0;
        switch (coupon.getType()) {
            case "满减":
                // 满减券必须满足最低消费金额
                if (originalPrice >= coupon.getMinAmount()) {
                    discount = coupon.getValue();
                }
                break;
            case "折扣":
                discount = Math.min(originalPrice * (1 - coupon.getValue()/10), coupon.getMaxAmount());
                break;
            case "立减":
                discount = Math.min(coupon.getValue(), originalPrice);
                break;
            case "秒杀":
                discount =  Math.min(20.0,(originalPrice-0.1));
                break;
            case "免单":
                discount =  Math.min(20.0,(originalPrice));
                break;
            default:
                throw new IllegalArgumentException("无效的优惠券类型");
        }
        return Math.min(discount, originalPrice); // 确保优惠不超过原价
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
        String voucherCode;
        boolean isUnique;
        int attempts = 0;
        final int MAX_ATTEMPTS = 10;

        do {
            // 使用更长的随机码以降低重复概率
            // 格式: 年月日(8位) + 时间戳后6位 + 随机数(6位)
            LocalDateTime now = LocalDateTime.now();
            String datePart = String.format("%04d%02d%02d", 
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());
            String timestampPart = String.format("%06d", 
                System.currentTimeMillis() % 1000000);
            String randomPart = String.format("%06d", 
                (int)(Math.random() * 1000000));
            
            voucherCode = datePart + timestampPart + randomPart;

            // 检查券码是否已存在
            isUnique = !orderRepository.existsByVoucherCode(voucherCode);
            attempts++;

            if (attempts >= MAX_ATTEMPTS) {
                // 如果多次尝试都失败，加入毫秒级时间戳作为后缀确保唯一性
                voucherCode = voucherCode + System.nanoTime();
                break;
            }
        } while (!isUnique);

        return voucherCode;
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