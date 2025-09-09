package com.dcm.demo.dto.response;

import com.dcm.demo.model.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Integer id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String cccd;
    private LocalDate birth;
    private User.Gender gender;
    private User.Role role;
    private String profileImage;
    private Boolean status;
    private LocalDateTime createdAt;
}
