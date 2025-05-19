package org.com.dianping.service;

import org.com.dianping.entity.*;
import org.com.dianping.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class InvitationService {
    private final InvitationRecordRepository recordRepo;
    private final InvitationRewardRepository rewardRepo;
    private final CouponService couponService;
    private final UserRepository userRepo;

    public InvitationService(InvitationRecordRepository recordRepo,
                             InvitationRewardRepository rewardRepo,
                             CouponService couponService,
                             UserRepository userRepo) {
        this.recordRepo = recordRepo;
        this.rewardRepo = rewardRepo;
        this.couponService = couponService;
        this.userRepo = userRepo;
    }

    @Transactional
    public void processInvitationReward(Long inviterId, Long inviteeId, Double orderAmount) {
        // 保存邀请记录
        InvitationRecord record = new InvitationRecord();
        record.setUserId(inviterId);
        record.setInviteeId(inviteeId);
        record.setOrderTime(LocalDateTime.now());
        record.setPrice(orderAmount);
        recordRepo.save(record);
        // 检查奖励条件
        long totalValid = recordRepo.countByUserId(inviterId);
        if (totalValid % 2 == 0) {
            createRewardCoupon(inviterId, inviteeId);
        }
    }

    private void createRewardCoupon(Long inviterId, Long inviteeId) {
        Coupon coupon = new Coupon();
        coupon.setUserId(inviterId);
        coupon.setCouponName("20元无门槛优惠券");
        coupon.setType("立减");
        coupon.setValue(20.0);
        coupon.setMinAmount(0.0);
        coupon.setCouponAmount(1);
        coupon.setExpireTime(LocalDateTime.now().plusDays(7));
        couponService.saveCoupon(coupon);

        InvitationReward reward = new InvitationReward();
        reward.setUserId(inviterId);
        reward.setInviteeId(inviteeId);
        reward.setCouponId(coupon.getId());
        reward.setTotalCount(recordRepo.countByUserId(inviterId));
        rewardRepo.save(reward);
    }
}