package com.bhumi.ragbot_ai.controller;

import com.bhumi.ragbot_ai.dto.RegisterRequest;
import com.bhumi.ragbot_ai.service.UserService;
import org.springframework.web.bind.annotation.*;
import com.bhumi.ragbot_ai.dto.LoginRequest;
import com.bhumi.ragbot_ai.dto.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        return userService.loginUser(request);
    }
}