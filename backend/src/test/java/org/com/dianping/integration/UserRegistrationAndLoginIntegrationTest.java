package org.com.dianping.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.dianping.DTO.LoginRequest;
import org.com.dianping.DTO.NewUserRequest;
import org.com.dianping.entity.User;
import org.com.dianping.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserRegistrationAndLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private final String testUsername = "integrationtestuser";
    private final String testPassword = "Password123";

    @BeforeEach
    void setUp() {
        // 清除可能存在的测试用户
        userRepository.findByUsername(testUsername)
                .ifPresent(user -> userRepository.delete(user));
    }

    @Test
    void completeUserRegistrationAndLoginFlow() throws Exception {
        // 1. 首先获取验证码
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(get("/captcha")
                .session(session))
                .andExpect(status().isOk());

        // 验证session中已存储验证码
        String captchaCode = (String) session.getAttribute("CAPTCHA_CODE");
        assertNotNull(captchaCode);

        // 2. 检查用户名是否可用
        mockMvc.perform(get("/users/check-username")
                .param("username", testUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", is(false)));

        // 3. 注册新用户
        NewUserRequest registerRequest = new NewUserRequest(testUsername, testPassword, captchaCode);
        mockMvc.perform(post("/users")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(testUsername)));

        // 验证用户已被保存到数据库
        assertTrue(userRepository.existsByUsername(testUsername));

        // 4. 再次检查用户名，应该显示已存在
        mockMvc.perform(get("/users/check-username")
                .param("username", testUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", is(true)));

        // 5. 使用新注册的用户信息登录
        LoginRequest loginRequest = new LoginRequest(testUsername, testPassword);
        MockHttpSession loginSession = new MockHttpSession();

        mockMvc.perform(post("/login")
                .session(loginSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(testUsername)));

        // 验证登录后会话中包含用户信息
        assertNotNull(loginSession.getAttribute("USER_SESSION"));
    }

    @Test
    void registrationShouldFail_WithInvalidCaptcha() throws Exception {
        // 1. 设置错误的验证码
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("CAPTCHA_CODE", "1234");

        // 2. 使用不匹配的验证码尝试注册
        NewUserRequest registerRequest = new NewUserRequest(testUsername, testPassword, "4321");
        mockMvc.perform(post("/users")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("验证码错误")));

        // 验证用户未被保存到数据库
        assertFalse(userRepository.existsByUsername(testUsername));
    }

    @Test
    void loginShouldFail_WithIncorrectPassword() throws Exception {
        // 1. 首先注册用户
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("CAPTCHA_CODE", "1234");

        NewUserRequest registerRequest = new NewUserRequest(testUsername, testPassword, "1234");
        mockMvc.perform(post("/users")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. 使用错误密码尝试登录
        LoginRequest loginRequest = new LoginRequest(testUsername, "WrongPassword123");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("密码错误")));
    }
}
