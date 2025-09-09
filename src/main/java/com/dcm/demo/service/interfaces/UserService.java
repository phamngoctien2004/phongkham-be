package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.ChangePasswordRequest;
import com.dcm.demo.dto.request.ResetPassword;
import com.dcm.demo.dto.request.UserRequest;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.model.User;

import java.util.List;

public interface UserService {
    User getUserByEmailOrPhone(String emailOrPhone);
    UserResponse findById(Integer id);
    //  create & update
    UserResponse createUser(UserRequest user);

    UserResponse updateUser(UserRequest user);

    void changePassword(ChangePasswordRequest request);

    void resetPassword(ResetPassword request);

    //  delete
    void deleteUser(Integer id);


}
