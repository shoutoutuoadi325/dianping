package org.com.dianping.service;
import java.util.List;

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

    public List<Coupon> getValidCouponsForUser(Long userId) {
        return couponRepository.findValidCouponsByUserId(userId);
    }

    public void issueNewUserCoupons(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("用户不存在"));
        
        if (user.getOrderCount() == 0) {
            // 创建四种优惠券
            Coupon coupon1 = new Coupon();
            coupon1.setCouponName("无门槛优惠券");
            coupon1.setUserId(userId);
            coupon1.setCouponAmount(1);
            
            Coupon coupon2 = new Coupon();
            coupon2.setCouponName("满30减8元");
            coupon2.setUserId(userId);
            coupon2.setCouponAmount(1);
            
            Coupon coupon3 = new Coupon();
            coupon3.setCouponName("满50减10元");
            coupon3.setUserId(userId);
            coupon3.setCouponAmount(1);
            
            Coupon coupon4 = new Coupon();
            coupon4.setCouponName("满100减20");
            coupon4.setUserId(userId);
            coupon4.setCouponAmount(1);
            
            // 保存优惠券
            couponRepository.save(coupon1);
            couponRepository.save(coupon2);
            couponRepository.save(coupon3);
            couponRepository.save(coupon4);
        }

    }
}
