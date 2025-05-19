package org.com.dianping.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "invitation_record")
//查看自己的邀请记录：包括成功邀请的新用户列表、邀请成功订单的下单时间与实付金额。
public class InvitationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long inviteeId;

    @Column(nullable = false)
    private LocalDateTime orderTime;//同order的createTime

    @Column(nullable = false)
    private Double price;

    public InvitationRecord() {
        super();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getInviteeId() {
        return inviteeId;
    }
    public void setInviteeId(Long inviteeId) {
        this.inviteeId = inviteeId;
    }
    public LocalDateTime getOrderTime() {
        return orderTime;
    }
    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
}
