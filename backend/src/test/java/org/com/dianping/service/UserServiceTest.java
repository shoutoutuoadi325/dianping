package org.com.dianping.service;

import org.com.dianping.DTO.LoginRequest;
import org.com.dianping.DTO.NewUserRequest;
import org.com.dianping.DTO.UserResponse;
import org.com.dianping.Utils.PasswordUtil;
import org.com.dianping.entity.User;
import org.com.dianping.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private String testPassword = "Password123";
    private String testUsername = "testuser";

    @BeforeEach
    void setUp() {
        String encryptedPassword = PasswordUtil.encryptPassword(testPassword);
        testUser = new User(1L, testUsername, encryptedPassword);
    }

    @Test
    void usernameExists_ShouldReturnTrue_WhenUsernameExists() {
        when(userRepository.existsByUsername(testUsername)).thenReturn(true);

        boolean result = userService.usernameExists(testUsername);

        assertTrue(result);
        verify(userRepository).existsByUsername(testUsername);
    }

    @Test
    void usernameExists_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        when(userRepository.existsByUsername(testUsername)).thenReturn(false);

        boolean result = userService.usernameExists(testUsername);

        assertFalse(result);
        verify(userRepository).existsByUsername(testUsername);
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameFormatIsInvalid() {
        NewUserRequest request = new NewUserRequest("abc", testPassword, "1234");

        Exception exception = assertThrows(RuntimeException.class, () -> userService.registerUser(request));

        assertEquals("用户名格式不合法", exception.getMessage());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordFormatIsInvalid() {
        NewUserRequest request = new NewUserRequest(testUsername, "12345", "1234");

        Exception exception = assertThrows(RuntimeException.class, () -> userService.registerUser(request));

        assertEquals("密码格式不合法", exception.getMessage());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        NewUserRequest request = new NewUserRequest(testUsername, testPassword, "1234");
        when(userRepository.existsByUsername(testUsername)).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.registerUser(request));

        assertEquals("用户名已存在", exception.getMessage());
        verify(userRepository).existsByUsername(testUsername);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldReturnUserResponse_WhenRegistrationIsSuccessful() {
        NewUserRequest request = new NewUserRequest(testUsername, testPassword, "1234");
        when(userRepository.existsByUsername(testUsername)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals(testUser.getUsername(), response.username());
        verify(userRepository).existsByUsername(testUsername);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loginUser_ShouldThrowException_WhenUserDoesNotExist() {
        LoginRequest request = new LoginRequest(testUsername, testPassword);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.loginUser(request));

        assertEquals("用户不存在", exception.getMessage());
        verify(userRepository).findByUsername(testUsername);
    }

    @Test
    void loginUser_ShouldThrowException_WhenPasswordIsIncorrect() {
        LoginRequest request = new LoginRequest(testUsername, "wrongPassword");
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(RuntimeException.class, () -> userService.loginUser(request));

        assertEquals("密码错误", exception.getMessage());
        verify(userRepository).findByUsername(testUsername);
    }

    @Test
    void loginUser_ShouldReturnUserResponse_WhenLoginIsSuccessful() {
        LoginRequest request = new LoginRequest(testUsername, testPassword);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.loginUser(request);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals(testUser.getUsername(), response.username());
        verify(userRepository).findByUsername(testUsername);
    }
}
