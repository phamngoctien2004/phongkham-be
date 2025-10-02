package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.model.User;
import com.dcm.demo.service.interfaces.ProfileLoader;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeService {
    private final List<ProfileLoader> loaders;
    private final UserService userService;
    private final UserMapper userMapper;
    public UserResponse me() {
        User user = userService.getCurrentUser();
        var roleData = loaders.stream()
                .filter(l -> l.supports(user.getRole()))
                .findFirst()
                .flatMap(ProfileLoader::loadProfile)
                .orElse(null); // có thể null nếu chưa có profile

        UserResponse response = userMapper.toResponse(user);
        response.setDoctor((DoctorResponse) roleData);
        return response;
    }
}
