package org.com.dianping.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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


    @Column(nullable = false)
    private Double originalPrice;

    @OneToOne
    private Coupon bestCoupon;

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
    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }
    public void setBestCoupon(Coupon bestCoupon) {
        this.bestCoupon = bestCoupon;
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