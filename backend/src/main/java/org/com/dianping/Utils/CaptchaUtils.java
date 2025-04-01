package org.com.dianping.Utils;

import jakarta.servlet.http.HttpSession;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaUtils {
    public static BufferedImage generateCaptchaImage(HttpSession session) {
        int width = 100, height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        
        Random rand = new Random();
        int number = 1000 + rand.nextInt(9000);
        String captchaCode = String.valueOf(number);
        session.setAttribute("CAPTCHA_CODE", captchaCode);
        
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(captchaCode, 10, 28);
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
            int x1 = rand.nextInt(width), y1 = rand.nextInt(height);
            int x2 = rand.nextInt(width), y2 = rand.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
        g.dispose();
        return image;
    }
}
