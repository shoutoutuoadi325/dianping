package org.com.dianping.controller;

import org.com.dianping.DTO.LoginRequest;
import org.com.dianping.DTO.UserResponse;
import org.com.dianping.service.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_WithValidCredentials_ShouldReturnUserResponse() {
        // Arrange
        LoginRequest request = new LoginRequest("testuser", "password");
        UserResponse expectedResponse = new UserResponse(1L, "testuser", 0);
        when(userService.loginUser(request)).thenReturn(expectedResponse);

        // Act
        UserResponse response = authController.login(request, session);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse.username(), response.username());
        assertEquals(expectedResponse.id(), response.id());
        verify(session).setAttribute("USER_SESSION", expectedResponse);
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        when(userService.loginUser(request)).thenThrow(new RuntimeException("密码错误"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authController.login(request, session));
        assertEquals("密码错误", exception.getMessage());
        verify(session, never()).setAttribute(any(), any());
    }
}
