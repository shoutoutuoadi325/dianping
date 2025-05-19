package org.com.dianping.DTO;

import org.com.dianping.entity.User;

public record UserResponse(
        Long id,
        String username,
        Integer orderCount,
        String invitationCode) {
    public UserResponse(User user) {
        this(user.getId(), user.getUsername(), user.getOrderCount(), user.getInvitationCode());
    }
}