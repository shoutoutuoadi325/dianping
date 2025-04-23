package org.com.dianping.DTO;

public class OrderRequest {
    private Long packageId;
    private Long couponId;

    public OrderRequest() {
    }
    public OrderRequest(Long packageId, Long couponId) {
        this.packageId = packageId;
        this.couponId = couponId;
    }
    public Long getPackageId() {
        return packageId;
    }
    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }
    public Long getCouponId() {
        return couponId;
    }
    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
}