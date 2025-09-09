package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.LoginRequest;
import com.dcm.demo.dto.request.OtpRequest;
import com.dcm.demo.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void sendOtp(OtpRequest request);
}

