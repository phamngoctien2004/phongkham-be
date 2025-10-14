package com.dcm.demo.controller;

import com.dcm.demo.dto.request.LoginRequest;
import com.dcm.demo.dto.request.OtpRequest;
import com.dcm.demo.dto.request.RegisterRequest;
import com.dcm.demo.dto.request.VerifyOtpRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.impl.SendMessage;
import com.dcm.demo.service.interfaces.AuthService;
import com.dcm.demo.service.interfaces.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final SendMessage sendMessage;
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
    @PostMapping("/register-otp")
    public ResponseEntity<?> sendRegister(@RequestBody OtpRequest request) {
        authService.sendRegisterOtp(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Send OTP successful")
        );
    }
        @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(authService.register(request),"Register successful")
        );
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(authService.canRegister(request),"Verify OTP successful")
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

