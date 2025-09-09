package com.dcm.demo.controller;

import com.dcm.demo.dto.request.LoginRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(
                new ApiResponse<>(authService.login(loginRequest),"Login successful")
        );
    }
}
