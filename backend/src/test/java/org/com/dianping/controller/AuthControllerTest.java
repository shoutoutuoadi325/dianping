package org.com.dianping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.dianping.DTO.LoginRequest;
import org.com.dianping.DTO.UserResponse;
import org.com.dianping.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void login_ShouldReturnUserResponse_WhenLoginIsSuccessful() throws Exception {
        String username = "testUser";
        String password = "Password123";
        LoginRequest request = new LoginRequest(username, password);
        UserResponse expectedResponse = new UserResponse(1L, username);

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(expectedResponse);

        MockHttpSession session = new MockHttpSession();

        MvcResult result = mockMvc.perform(post("/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is(username)))
                .andReturn();

        // 验证会话属性
        UserResponse sessionUser = (UserResponse) session.getAttribute("USER_SESSION");
        assertNotNull(sessionUser);
        assertEquals(expectedResponse.id(), sessionUser.id());
        assertEquals(expectedResponse.username(), sessionUser.username());

        verify(userService).loginUser(any(LoginRequest.class));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenUserServiceThrowsException() throws Exception {
        String username = "nonExistentUser";
        String password = "Password123";
        LoginRequest request = new LoginRequest(username, password);

        when(userService.loginUser(any(LoginRequest.class))).thenThrow(new RuntimeException("用户不存在"));

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("用户不存在")));

        verify(userService).loginUser(any(LoginRequest.class));
    }
}
