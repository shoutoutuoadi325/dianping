package org.com.dianping.service;

import java.util.List;

import org.com.dianping.entity.Review;
import org.com.dianping.repository.MerchantRepository;
import org.com.dianping.repository.ReviewRepository;
import org.com.dianping.repository.UserRepository;
import org.springframework.stereotype.Service;
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;    
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;

    public ReviewService(ReviewRepository reviewRepository, MerchantRepository merchantRepository, UserRepository userRepository, CouponService couponService) {
        this.reviewRepository = reviewRepository;
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
        this.couponService = couponService;
    }

    public List<Review> getReviewsByMerchantID(Long merchantId) {
        List<Review> reviews = reviewRepository.findByMerchantID(merchantId);
        return reviews;
    }

    public List<Review> getReviewsByUserID(Long userId) {
        List<Review> reviews = reviewRepository.findByUserID(userId);
        return reviews;
    }

    public List<Review> getReviewsByParentID(Long parentId) {
        List<Review> reviews = reviewRepository.findByParentID(parentId);
        return reviews;
    }

    public void createReview(Review review) {
        if (merchantRepository.existsById(review.getMerchantID())) {
            if (userRepository.existsById(review.getUserID())) {
                if(getReviewsByUserID(review.getUserID()).size() == 2 && review.getComment().length() >= 15) {
                    couponService.issueNewUserCoupons(review.getUserID(), 'E');
                    reviewRepository.save(review);
                }else{
                    reviewRepository.save(review);
                }
            }
        }
    }

}
