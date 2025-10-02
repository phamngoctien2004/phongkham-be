package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.response.ProfileData;
import com.dcm.demo.model.User;

import java.util.Optional;

public interface ProfileLoader {
    boolean supports(User.Role role);
    Optional<? extends ProfileData> loadProfile();
}
