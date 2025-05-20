package org.com.dianping.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.com.dianping.entity.Coupon;
import org.com.dianping.entity.User;
import org.com.dianping.repository.CouponRepository;
import org.com.dianping.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    public CouponService(CouponRepository couponRepository, UserRepository userRepository) {
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
    }

    public List<Coupon> getValidCouponsForUser(Long userId, String merchantCategory) {
        List<Coupon> validCoupons = couponRepository.findValidCouponsByUserId(userId);
        
        // 过滤出适用当前商家种类的优惠券
        return validCoupons.stream()
            .filter(coupon -> 
                coupon.getCategory() == null ||  // 适用所有种类
                coupon.getCategory().equals(merchantCategory)  // 种类匹配
            )
            .collect(Collectors.toList());
    }

    public List<Coupon> getAllCouponsByUserId(Long userId) {
        return couponRepository.findByUserId(userId); // 确保此方法在 Repository 中正确实现
    }

    public void issueNewUserCoupons(Long userId, char choice) {
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("用户不存在"));
        
        if (user.getOrderCount() != 0) {
            return;
        }
    
        Coupon coupon_init = new Coupon();
        coupon_init.setUserId(userId);
        coupon_init.setExpireTime(null);
        coupon_init.setShopId(null);
        coupon_init.setCouponAmount(1);
        coupon_init.setMaxAmount(9999999999999999.0);
        switch (choice) {
            case 'A':
                coupon_init.setType("满减");
                coupon_init.setCategory("火锅");
                coupon_init.setMiniAmount(100.0);
                coupon_init.setValue(38.0);
                coupon_init.setCouponName("满100减38元(火锅专用券)");
                coupon_init.setExpireTime(LocalDateTime.now().plusDays(7));
                break;
            case 'B':
                coupon_init.setType("折扣");
                coupon_init.setMiniAmount(9.0);
                coupon_init.setValue(8.0);
                coupon_init.setCouponName("满9元8折券(喜茶专用券)");
                coupon_init.setExpireTime(LocalDateTime.now().plusDays(7));
                coupon_init.setShopId(32L);
                break;
            case 'C':
                coupon_init.setType("秒杀");
                coupon_init.setCategory("奶茶");
                coupon_init.setMiniAmount(0.0);
                coupon_init.setValue(0.0);
                coupon_init.setCouponName("奶茶畅喝秒杀券");
                coupon_init.setExpireTime(LocalDateTime.now().plusDays(1));
                break; 
            case 'D':
                coupon_init.setType("立减");
                coupon_init.setCategory(null);
                coupon_init.setMiniAmount(0.0);
                coupon_init.setValue(10.0);
                coupon_init.setCouponName("通用立减10元券");
                break; 
            case 'E':
                coupon_init.setType("折扣");
                coupon_init.setCategory(null);
                coupon_init.setMiniAmount(0.0);
                coupon_init.setValue(8.0);
                coupon_init.setCouponName("通用8折券,最高抵扣20元");
                coupon_init.setExpireTime(LocalDateTime.now().plusDays(7));
                coupon_init.setMaxAmount(20.0);
                break;
            default:
                throw new IllegalArgumentException("无效的选择类型");
        }
        
        couponRepository.save(coupon_init);
    }
    
    public void saveCoupon(Coupon coupon) {
        couponRepository.save(coupon);
    }

    public void useCoupon(Long couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        if (optionalCoupon.isPresent()) {
            Coupon coupon = optionalCoupon.get();
            coupon.setCouponAmount(coupon.getCouponAmount() - 1);
            couponRepository.save(coupon);
        }
    }
}
