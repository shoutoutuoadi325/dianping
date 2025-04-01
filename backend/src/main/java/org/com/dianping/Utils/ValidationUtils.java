package org.com.dianping.Utils;

import jakarta.servlet.http.HttpSession;
import org.com.dianping.DTO.NewUserRequest;

public class ValidationUtils {
    public static void validateNewUserRequest(NewUserRequest request, HttpSession session) {
        String sessionCaptcha = (String) session.getAttribute("CAPTCHA_CODE");
        if (sessionCaptcha == null || !sessionCaptcha.equals(request.captcha())) {
            throw new RuntimeException("验证码错误");
        }
        if (!request.username().matches("^[A-Za-z0-9_]{4,20}$")) {
            throw new RuntimeException("用户名格式不合法");
        }
        if (!request.password().matches("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")) {
            throw new RuntimeException("密码格式不合法");
        }
        session.removeAttribute("CAPTCHA_CODE");
    }
}
