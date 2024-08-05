package com.wordpress.shopBuilder.controller;

import com.wordpress.shopBuilder.dto.UserLoginDto;
import com.wordpress.shopBuilder.dto.UserRegistrationDto;
import com.wordpress.shopBuilder.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "True")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        return userService.registerUser(registrationDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserLoginDto loginDto) {
        return userService.loginUser(loginDto);
    }

}
