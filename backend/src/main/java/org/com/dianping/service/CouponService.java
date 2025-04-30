package org.com.dianping.service;
import java.util.List;
import java.util.Optional;

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

    public void issueNewUserCoupons(Long userId, char choice) {
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("用户不存在"));
        
        if (user.getOrderCount() == 0) {
            // 创建四种优惠券
            Coupon coupon_init = new Coupon();

            if(choice == 'A'){
                coupon_init.setCouponName("无门槛优惠券");
                coupon_init.setUserId(userId);
                coupon_init.setCouponAmount(1);
            }else if(choice == 'B'){
                coupon_init.setCouponName("满30减8元");
                coupon_init.setUserId(userId);
                coupon_init.setCouponAmount(1);
            }else if(choice == 'C'){
                coupon_init.setCouponName("0元免单券(10元以内)");
                coupon_init.setUserId(userId);
                coupon_init.setCouponAmount(1);
            }else if(choice == 'D'){
                coupon_init.setCouponName("满100打8折券");
                coupon_init.setUserId(userId);
                coupon_init.setCouponAmount(1);
            }

            
            // 保存优惠券
            couponRepository.save(coupon_init);
        }

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
