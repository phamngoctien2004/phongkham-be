package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.*;
import com.dcm.demo.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse loginDashboard(LoginRequest request);
    void sendOtp(OtpRequest request);
    void sendRegisterOtp(OtpRequest request);
    boolean canRegister(VerifyOtpRequest request);
    LoginResponse register(RegisterRequest request);
    void resetPassword(ResetPasswordRequest request);
}

