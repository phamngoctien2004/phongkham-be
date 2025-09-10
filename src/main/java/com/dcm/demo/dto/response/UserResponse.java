package com.dcm.demo.dto.response;

import com.dcm.demo.model.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Integer id;
    private String email;
    private String phone;
    private User.Role role;
    private Boolean status;
    private LocalDateTime createdAt;
}
