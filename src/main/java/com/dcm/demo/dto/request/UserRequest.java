package com.dcm.demo.dto.request;

import com.dcm.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private int id;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String address;
    private String cccd;
    private LocalDate birth;
    private User.Gender gender;
    private User.Role role;
}
