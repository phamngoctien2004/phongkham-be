package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.LoginRequest;
import com.dcm.demo.dto.response.LoginResponse;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.model.User;
import com.dcm.demo.service.interfaces.AuthService;
import com.dcm.demo.service.interfaces.JwtService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private UserMapper userMapper;
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userService.getUserByEmailOrPhone(request.getUsername());
        if(user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.AUTH_FAILED);
        }

        return new LoginResponse(
                jwtService.generate(user.getId(), user.getRole().name(), 60),
                userMapper.toResponse(user)
        );
    }
}
