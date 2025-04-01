package org.com.dianping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.dianping.DTO.NewUserRequest;
import org.com.dianping.DTO.UserResponse;
import org.com.dianping.service.UserService;
import org.com.dianping.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void checkUsernameAvailability_ShouldReturnExistsTrue_WhenUsernameExists() throws Exception {
        String username = "existingUser";
        when(userService.usernameExists(username)).thenReturn(true);

        mockMvc.perform(get("/users/check-username")
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exists", is(true)));

        verify(userService).usernameExists(username);
    }

    @Test
    void checkUsernameAvailability_ShouldReturnExistsFalse_WhenUsernameDoesNotExist() throws Exception {
        String username = "newUser";
        when(userService.usernameExists(username)).thenReturn(false);

        mockMvc.perform(get("/users/check-username")
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exists", is(false)));

        verify(userService).usernameExists(username);
    }

    @Test
    void registerUser_ShouldReturnUserResponse_WhenRegistrationIsSuccessful() throws Exception {
        String username = "newUser";
        String password = "Password123";
        String captcha = "1234";
        NewUserRequest request = new NewUserRequest(username, password, captcha);
        UserResponse expectedResponse = new UserResponse(1L, username);

        // 使用工具类创建 session
        MockHttpSession session = TestUtils.createSession(captcha);

        when(userService.registerUser(any(NewUserRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/users")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is(username)));

        verify(userService).registerUser(any(NewUserRequest.class));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenCaptchaIsInvalid() throws Exception {
        String username = "newUser";
        String password = "Password123";
        String requestCaptcha = "1234";
        String sessionCaptcha = "5678";
        NewUserRequest request = new NewUserRequest(username, password, requestCaptcha);

        // 使用工具类创建 session
        MockHttpSession session = TestUtils.createSession(sessionCaptcha);

        mockMvc.perform(post("/users")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("验证码错误")));

        verify(userService, never()).registerUser(any(NewUserRequest.class));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenUsernameFormatIsInvalid() throws Exception {
        String username = "abc"; // 用户名过短
        String password = "Password123";
        String captcha = "1234";
        NewUserRequest request = new NewUserRequest(username, password, captcha);

        // 使用工具类创建 session
        MockHttpSession session = TestUtils.createSession(captcha);

        mockMvc.perform(post("/users")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("用户名格式不合法")));

        verify(userService, never()).registerUser(any(NewUserRequest.class));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenPasswordFormatIsInvalid() throws Exception {
        String username = "validUser";
        String password = "12345"; // 密码格式不合法
        String captcha = "1234";
        NewUserRequest request = new NewUserRequest(username, password, captcha);

        // 使用工具类创建 session
        MockHttpSession session = TestUtils.createSession(captcha);

        mockMvc.perform(post("/users")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("密码格式不合法")));

        verify(userService, never()).registerUser(any(NewUserRequest.class));
    }
}
