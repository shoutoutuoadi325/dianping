package org.com.dianping.controller;

import java.util.List;

import org.com.dianping.entity.Review;
import org.com.dianping.service.ReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/merchant/{merchantID}")
    public List<Review> getReviewsByMerchantID(@PathVariable Long merchantID) {
        return reviewService.getReviewsByMerchantID(merchantID);
    }
    @GetMapping("/user/{userID}")
    public List<Review> getReviewsByUserID(@PathVariable Long userID) {
        return reviewService.getReviewsByUserID(userID);
    }
    @GetMapping("/parent/{parentID}")
    public List<Review> getReviewsByParentID(@PathVariable Long parentID) {
        return reviewService.getReviewsByParentID(parentID);
    }

    @PostMapping("/add")
    public void addReview(@RequestBody Review review) {
        reviewService.createReview(review);
    }
}
