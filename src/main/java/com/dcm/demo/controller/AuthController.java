package com.dcm.demo.controller;

import com.dcm.demo.dto.request.LoginRequest;
import com.dcm.demo.dto.request.OtpRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.AuthService;
import com.dcm.demo.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(
                new ApiResponse<>(authService.login(loginRequest),"Login successful")
        );
    }
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {
        authService.sendOtp(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Send OTP successful")
        );
    }
    @PostMapping("/dashboard/login")
    public ResponseEntity<?> dashboardLogin(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(
                new ApiResponse<>(authService.loginDashboard(loginRequest),"Login successful")
        );
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(
                new ApiResponse<>(jwtService.generate(1, "BENH_NHAN", 1000000000), "Test successful")
        );
    }
}

