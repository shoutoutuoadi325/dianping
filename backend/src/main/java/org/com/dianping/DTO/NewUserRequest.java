package org.com.dianping.DTO;

public record NewUserRequest(
                String username,
                String password,
                String captcha // 新增验证码字段
) {
}
