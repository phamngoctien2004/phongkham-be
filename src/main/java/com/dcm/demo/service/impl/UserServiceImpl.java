package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.ChangePasswordRequest;
import com.dcm.demo.dto.request.ResetPassword;
import com.dcm.demo.dto.request.UserRequest;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.UserRepository;
import com.dcm.demo.service.interfaces.JwtService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Override
    public User getUserByEmailOrPhone(String emailOrPhone) {
        Optional<User> user = userRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        return user.orElse(null);
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone).orElse(null);
    }

    @Override
    public UserResponse findById(Integer id) {
        return userMapper.toResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND))
        );
    }

    @Override
    public UserResponse createUser(UserRequest user) {
        User userEntity = userMapper.toEntity(user);
        return userMapper.toResponse(userRepository.save(userEntity));
    }

    @Override
    public User createUserEntity(UserRequest user) {
        User userEntity = userMapper.toEntity(user);
        return userRepository.save(userEntity);
    }

    @Override
    public UserResponse updateUser(UserRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setCccd(request.getCccd());
        user.setBirth(request.getBirth());
        user.setGender(request.getGender());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {

    }

    @Override
    public void resetPassword(ResetPassword request) {

    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int id = Integer.parseInt(authentication.getName());
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
    }


}
