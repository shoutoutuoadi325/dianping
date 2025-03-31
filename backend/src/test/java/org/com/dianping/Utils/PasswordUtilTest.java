package org.com.dianping.Utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void encryptPassword_ShouldReturnEncryptedPassword() {
        String password = "Password123";

        String encryptedPassword = PasswordUtil.encryptPassword(password);

        assertNotNull(encryptedPassword);
        assertNotEquals(password, encryptedPassword);
    }

    @Test
    void encryptPassword_ShouldReturnDifferentEncryptionsForSamePassword() {
        String password = "Password123";

        String encryptedPassword1 = PasswordUtil.encryptPassword(password);
        String encryptedPassword2 = PasswordUtil.encryptPassword(password);

        assertNotEquals(encryptedPassword1, encryptedPassword2);
    }

    @Test
    void checkPassword_ShouldReturnTrue_WhenPasswordMatches() {
        String password = "Password123";
        String encryptedPassword = PasswordUtil.encryptPassword(password);

        boolean result = PasswordUtil.checkPassword(password, encryptedPassword);

        assertTrue(result);
    }

    @Test
    void checkPassword_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        String password = "Password123";
        String wrongPassword = "WrongPassword123";
        String encryptedPassword = PasswordUtil.encryptPassword(password);

        boolean result = PasswordUtil.checkPassword(wrongPassword, encryptedPassword);

        assertFalse(result);
    }
}
