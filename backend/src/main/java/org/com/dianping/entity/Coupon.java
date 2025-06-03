package org.com.dianping.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_name", nullable = false, unique = false)
    private String couponName;

    @Column(name = "user_id", nullable = false, unique = false)
    private Long userId;

    @Column(name = "coupon_amount", nullable = false, columnDefinition = "INTEGER")
    private Integer couponAmount;

    @Column(name = "category", nullable = true)
    private String category;  // 商品类别改为String类型

    @Column(name = "shop_id", nullable = true)
    private Long shopId;  // 店铺ID，null表示适用所有店铺

    @Column(name = "type", nullable = false)
    private String type;  // 优惠券类型，例如：满减、折扣等

    @Column(name = "value", nullable = true)
    private Double value;  // 优惠券的值，例如：满减券的减额金额，折扣券的折扣比例 

    @Column(name = "MaxAmount", nullable = true)
    private Double maxAmount;  // 最大减免

    @Column(name = "minAmount", nullable = true)
    private Double minAmount;  // 最小消费金额，例如：满30减8元的30
    @Column(name = "expire_time", nullable = true)
    private LocalDateTime expireTime;  // 失效时间，null表示永久有效

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(Integer couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }
    public Double getMinAmount() {
        return minAmount;
    }
    public void setMiniAmount(Double miniAmount) {
        this.minAmount = miniAmount;
    }
    public Double getMaxAmount() {
        return maxAmount;
    }
    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }
}
