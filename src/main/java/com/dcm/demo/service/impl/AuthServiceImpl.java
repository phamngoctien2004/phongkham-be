package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.*;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.LoginResponse;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Patient;
import com.dcm.demo.model.User;
import com.dcm.demo.service.interfaces.AuthService;
import com.dcm.demo.service.interfaces.JwtService;
import com.dcm.demo.service.interfaces.PatientService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SendMessage sendMessage;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${ID_PHONE}")
    private String ip_phone;
    @Value("${API_KEY_PHONE}")
    private String phone_api_key;


    @Override
    public LoginResponse login(LoginRequest request) {
        if (request.getType().equals("OTP")) {
            return loginOtp(request);
        }
        return loginPassword(request);
    }

    private LoginResponse loginOtp(LoginRequest request) {
        User user = userService.getUserByEmailOrPhone(request.getUsername());
        Object value = redisTemplate.opsForValue().get("otp:" + request.getUsername());
        if (user == null || value == null) {
            throw new AppException(ErrorCode.AUTH_FAILED);
        }
        redisTemplate.delete("otp:" + request.getUsername());

        Patient patient = patientService.findByPhone(user.getPhone());
        UserResponse userResponse = userMapper.toResponse(user);
        userResponse.setName(patient.getFullName());


        userResponse.setCreatedPassword(user.getPassword() != null && !user.getPassword().isEmpty());
        return new LoginResponse(
                jwtService.generate(user.getId(), user.getRole().name(), 60),
                userResponse
        );
    }

    private LoginResponse loginPassword(LoginRequest request) {
        User user = userService.getUserByEmailOrPhone(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.AUTH_FAILED);
        }

        Patient patient = patientService.findByPhone(user.getPhone());
        UserResponse userResponse = userMapper.toResponse(user);
        userResponse.setName(patient.getFullName());
        return new LoginResponse(
                jwtService.generate(user.getId(), user.getRole().name(), 60),
                userResponse
        );
    }

    @Override
    public LoginResponse loginDashboard(LoginRequest request) {
        User user = userService.findByEmail(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.AUTH_FAILED);
        }

        UserResponse userResponse = userMapper.toResponse(user);
        userResponse.setRole(user.getRole());
        Doctor doctor = user.getDoctor();
        if (doctor != null) {
            DoctorResponse doctorResponse = new DoctorResponse();
            doctorResponse.setId(doctor.getId());
            doctorResponse.setFullName(doctor.getFullName());
            userResponse.setDoctor(doctorResponse);
        }
        return new LoginResponse(
                jwtService.generate(user.getId(), user.getRole().name(), 600),
                userMapper.toResponse(user)
        );
    }

    @Override
    public void sendOtp(OtpRequest request) {
        String phone = request.getTo();
        if (userService.findByPhone(phone).isEmpty()) {
            throw new RuntimeException(" Số điện thoại chưa được đăng ký");
        }
        Random random = new Random();
        Integer number = 100000 + random.nextInt(900000);
        System.out.println("Random 6 digits: " + number);

        request.setMessage("Mã OTP của bạn là: " + number);
//        org.springframework.http.HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
//        headers.add("Authorization", phone_api_key);
//        HttpEntity<?> httpEntity = new HttpEntity<>(request, headers);
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.postForEntity(
//                ip_phone,
//                httpEntity,
//                OtpRequest.class
//        );
        redisTemplate.opsForValue().set("otp:" + request.getTo(), number, 300, TimeUnit.SECONDS);
    }

    @Override
    public void sendRegisterOtp(OtpRequest request) {
        Optional<User> user = userService.findByPhone(request.getTo());
        if(user.isPresent()){
            throw new RuntimeException(" Số điện thoại đã được đăng ký");
        }
        sendMessage.sendOtp(request);
    }

    @Override
    @Transactional
    public boolean canRegister(VerifyOtpRequest request) {
        sendMessage.checkOtp(request.getPhone(), request.getOtp());
//        Patient patient = patientService.findByPhone(request.getPhone());

//      thong tin benh nhan da ton tai -> tao user -> dang nhap
//        if(patient != null){
//            UserRequest userRequest = new UserRequest();
//            userRequest.setPhone(request.getPhone());
//            userRequest.setRole(User.Role.BENH_NHAN);
//            User user = userService.createUserEntity(userRequest);
//            patient.getRelationships().add(patientService.buildRelationship(patient, user, "Bản thân"));
//            patientService.save(patient);
//            UserResponse userResponse = userMapper.toResponse(user);
//            userResponse.setName(patient.getFullName());
//            return new LoginResponse(
//                    jwtService.generate(user.getId(), user.getRole().name(), 600),
//                    userResponse
//            );
//        }
        return true;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("Mật khẩu không khớp");
        }
        UserRequest userRequest = new UserRequest();
        userRequest.setPhone(request.getPhone());
        userRequest.setEmail(request.getEmail());
        userRequest.setPassword(passwordEncoder.encode(request.getPassword()));
        userRequest.setRole(User.Role.BENH_NHAN);
        User user = userService.createUserEntity(userRequest);

        PatientRequest patientRequest = new PatientRequest();
        patientRequest.setFullName(request.getName());
        patientRequest.setPhone(request.getPhone());
        patientRequest.setEmail(request.getEmail());
        patientRequest.setGender(request.getGender());
        patientRequest.setBirth(request.getBirth());
        PatientResponse patient = patientService.createPatient(patientRequest, user);

        UserResponse userResponse = userMapper.toResponse(user);
        userResponse.setName(patient.getFullName());
        return new LoginResponse(
                jwtService.generate(user.getId(), user.getRole().name(), 600),
                userResponse
        );
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("Mật khẩu không khớp");
        }
//      user thiet lap
        if(request.getUserId() == null){
            User user = userService.getCurrentUser();
            if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
                throw new RuntimeException("Mật khẩu cũ không đúng");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            userService.save(user);
            return;
        }
//      admin thiet lap
        User user = userService.getById(request.getUserId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.save(user);
    }
}


