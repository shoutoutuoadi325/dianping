package org.com.dianping.DTO;

import org.com.dianping.entity.Merchant;
import org.com.dianping.entity.User;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DTOTest {

    @Test
    void userResponse_ShouldMapFromUser() {
        // given
        User user = new User(1L, "testuser", "encryptedPassword");

        // when
        UserResponse response = new UserResponse(user);

        // then
        assertEquals(1L, response.id());
        assertEquals("testuser", response.username());
    }

    @Test
    void merchantResponse_ShouldMapFromMerchant() {
        // given
        Date now = new Date(System.currentTimeMillis());
        Merchant merchant = new Merchant(
                1L, "Test Merchant", "Restaurant", 4.5, "Test Location",
                100.0, 12345678L, now, now, "Test Tags", "test-url");

        // when
        MerchantResponse response = new MerchantResponse(merchant);

        // then
        assertEquals(1L, response.id());
        assertEquals("Test Merchant", response.merchantName());
        assertEquals("Restaurant", response.type());
        assertEquals(4.5, response.score());
        assertEquals("Test Location", response.location());
        assertEquals(100.0, response.averageConsumption());
        assertEquals(12345678L, response.telephoneNumber());
        assertEquals(now, response.startTime());
        assertEquals(now, response.endTime());
        assertEquals("Test Tags", response.tags());
        assertEquals("test-url", response.photoUrl());
    }

    @Test
    void loginRequest_ShouldStoreValues() {
        // when
        LoginRequest request = new LoginRequest("testuser", "password");

        // then
        assertEquals("testuser", request.username());
        assertEquals("password", request.password());
    }

    @Test
    void newUserRequest_ShouldStoreValues() {
        // when
        NewUserRequest request = new NewUserRequest("testuser", "password", "1234");

        // then
        assertEquals("testuser", request.username());
        assertEquals("password", request.password());
        assertEquals("1234", request.captcha());
    }
}
