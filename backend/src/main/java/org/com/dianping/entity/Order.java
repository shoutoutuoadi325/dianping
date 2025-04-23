package org.com.dianping.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "package_id")
    private Long packageId;

    @Column(name = "coupon_code", unique = true)
    private String couponCode;

    private Double packagePrice;
    private Double discount;
    private Double finalPrice;

    @Column(name = "business_id")
    private Long businessId;

    private LocalDateTime createTime = LocalDateTime.now();
}