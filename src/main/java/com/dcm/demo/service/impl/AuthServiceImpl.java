package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.LoginRequest;
import com.dcm.demo.dto.request.OtpRequest;
import com.dcm.demo.dto.response.LoginResponse;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.model.Patient;
import com.dcm.demo.model.User;
import com.dcm.demo.service.interfaces.AuthService;
import com.dcm.demo.service.interfaces.JwtService;
import com.dcm.demo.service.interfaces.PatientService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PatientService patientService;
    private UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${ID_PHONE}")
    private String ip_phone;
    @Value("${API_KEY_PHONE}")
    private String phone_api_key;



    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userService.getUserByEmailOrPhone(request.getUsername());
        Object value =  redisTemplate.opsForValue().get("otp:" + request.getUsername());
        if(user == null || value == null) {
            throw new AppException(ErrorCode.AUTH_FAILED);
        }
        redisTemplate.delete("otp:" + request.getUsername());

        Patient patient = patientService.findByPhone(user.getPhone());
        UserResponse userResponse = userMapper.toResponse(user);
        userResponse.setName(patient.getFullName());
        return new LoginResponse(
                jwtService.generate(user.getId(), user.getRole().name(), 60),
                userResponse
        );
    }

    @Override
    public void sendOtp(OtpRequest request) {
        String phone = request.getTo();
        if(userService.findByPhone(phone) == null){
            throw new RuntimeException(" Số điện thoại chưa được đăng ký" );
        }
        Random random = new Random();
        Integer number = 100000 + random.nextInt(900000);
        System.out.println("Random 6 digits: " + number);

        request.setMessage("Mã OTP của bạn là: " + number);
        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.add("Authorization", phone_api_key);
        HttpEntity<?> httpEntity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(
                ip_phone,
                httpEntity,
                OtpRequest.class
        );
        redisTemplate.opsForValue().set("otp:" + request.getTo(), number, 300, TimeUnit.SECONDS);
    }
}


