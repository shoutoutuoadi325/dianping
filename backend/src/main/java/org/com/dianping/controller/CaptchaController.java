package org.com.dianping.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.com.dianping.Utils.CaptchaUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class CaptchaController {
    @GetMapping("/captcha")
    public void getCaptcha(HttpSession session, HttpServletResponse response) throws IOException {
        // 将验证码生成逻辑委托给 CaptchaUtils
        BufferedImage image = CaptchaUtils.generateCaptchaImage(session);
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        ImageIO.write(image, "png", response.getOutputStream());
    }
}
