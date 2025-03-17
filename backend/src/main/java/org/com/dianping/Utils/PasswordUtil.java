package org.com.dianping.Utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//生成加密密码
public class PasswordUtil {
   private static final BCryptPasswordEncoder passwordEncoder =  new BCryptPasswordEncoder();

   public static final String encryptPassword(String password) {
       return passwordEncoder.encode(password);
   }
   public static boolean checkPassword(String password, String encryptPassword) {
       return passwordEncoder.matches(password, encryptPassword);
   }
}

