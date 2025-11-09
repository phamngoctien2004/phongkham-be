package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.ChangePasswordRequest;
import com.dcm.demo.dto.request.EmailRequest;
import com.dcm.demo.dto.request.ResetPassword;
import com.dcm.demo.dto.request.UserRequest;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Page<UserResponse> findAll(Pageable pageable, String keyword, User.Role role);

    List<User> findAll();
    User getUserByEmailOrPhone(String emailOrPhone);

    UserResponse findById(Integer id);

    Optional<User> findByPhone(String phone);

    User getById(Integer id);

    User findByEmail(String email);

    //  create & update
    UserResponse createUser(UserRequest user);

    User createAccountByPhone(String phone);

    User createAccountByCCCD(String cccd);

    User createUserEntity(UserRequest user);

    UserResponse updateUser(UserRequest user);

    void changePassword(ChangePasswordRequest request);

    void resetPassword(ResetPassword request);

    //  delete
    void deleteUser(Integer id);

    User save(User user);

    User getCurrentUser();

}
