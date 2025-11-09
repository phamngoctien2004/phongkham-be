package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.ChangePasswordRequest;
import com.dcm.demo.dto.request.EmailRequest;
import com.dcm.demo.dto.request.ResetPassword;
import com.dcm.demo.dto.request.UserRequest;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.UserRepository;
import com.dcm.demo.service.interfaces.EmailService;
import com.dcm.demo.service.interfaces.NotificationService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return userRepository.findByRole(User.Role.BENH_NHAN);
    }

    @Override
    public Page<UserResponse> findAll(Pageable pageable, String keyword, User.Role role) {
        System.out.println("ok2");
        return userRepository.findAll(pageable, keyword, role)
                .map(this::toResponse);
    }

    private UserResponse toResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setRole(user.getRole());
        userResponse.setStatus(user.getStatus());
        return userResponse;
    }

    @Override
    public User getUserByEmailOrPhone(String emailOrPhone) {
        Optional<User> user = userRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        return user.orElse(null);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public User getById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserResponse findById(Integer id) {
        return userMapper.toResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND)));
    }

    @Override
    public UserResponse createUser(UserRequest user) {
        User userEntity = userMapper.toEntity(user);
        return userMapper.toResponse(userRepository.save(userEntity));
    }

    @Override
    public User createAccountByPhone(String phone) {
        User existUser = userRepository.findByPhone(phone).orElse(null);
        if (existUser != null) {
            return existUser;
        }
        User user = new User();
        user.setPhone(phone);
        user.setRole(User.Role.BENH_NHAN);
        return userRepository.save(user);
    }

    @Override
    public User createAccountByCCCD(String cccd) {
        return null;
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
        user.setName(request.getName());
        user.setRole(request.getRole());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(ResetPassword request) {

    }

    @Override
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(user.getRelationships() != null && !user.getRelationships().isEmpty()){
            throw new RuntimeException( "Cannot delete user with existing relationships");
        }
        userRepository.deleteById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        log.info("🔍 Getting current user from SecurityContext...");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.error("❌ Authentication is NULL in SecurityContext!");
            throw new AppException(ErrorCode.RECORD_NOTFOUND);
        }

        log.info("✅ Authentication found: {} (Type: {})",
                authentication.getName(),
                authentication.getClass().getSimpleName());

        int id = Integer.parseInt(authentication.getName());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));

        log.info("✅ User found: {} (ID: {})", user.getName(), user.getId());
        return user;
    }


}
