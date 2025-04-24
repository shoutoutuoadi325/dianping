package org.com.dianping.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long packageId;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private String businessName;

    private Long couponId;

    @Column(nullable = false)
    private Double originalPrice;

    @Column(nullable = false)
    private Double finalPrice;

    @Column(nullable = false, unique = true)
    private String voucherCode;

    @Column(nullable = false)
    private String status; // 订单状态：未使用/已使用

    public Order() { super(); }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }
    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }
    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Long getId() {
        return id;
    }
}