package org.com.dianping.controller;

import org.com.dianping.DTO.NewUserRequest;
import org.com.dianping.DTO.UserResponse;
import org.com.dianping.service.UserService;
import org.com.dianping.Utils.ValidationUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check-username")
    public Map<String, Boolean> checkUsernameAvailability(@RequestParam String username) {
        boolean exists = userService.usernameExists(username);
        return Collections.singletonMap("exists", exists);
    }

    @PostMapping
    public UserResponse registerUser(@RequestBody NewUserRequest request, HttpSession session) {
        // 将具体验证逻辑委托给 ValidationUtils
        ValidationUtils.validateNewUserRequest(request, session);
        return userService.registerUser(request);
    }
}