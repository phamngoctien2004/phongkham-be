package com.dcm.demo.dto.request;

import com.dcm.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private Integer id;
    private String email;
    private String password;
    private String phone;
    private User.Role role;
}
