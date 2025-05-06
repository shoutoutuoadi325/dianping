package org.com.dianping.service;
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

    public void issueNewUserCoupons(Long userId, char choice) {
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("用户不存在"));
        
        if (user.getOrderCount() != 0) {
            return;
        }
    
        Coupon coupon_init = new Coupon();
        coupon_init.setUserId(userId);
        coupon_init.setCouponAmount(1);
    
        switch (choice) {
            case 'A':
                coupon_init.setCouponName("无门槛优惠券");
                break;
            case 'B':
                coupon_init.setCouponName("满30减8元");
                break;
            case 'C':
                coupon_init.setCouponName("0元免单券(10元以内)");
                break;
            case 'D':
                coupon_init.setCouponName("满100打8折券(最多可减30元)");
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
            if (coupon.getCouponAmount() > 1) {
                coupon.setCouponAmount(coupon.getCouponAmount() - 1);
                couponRepository.save(coupon);
            } else {
                couponRepository.delete(coupon);
            }
        }
    }
}
