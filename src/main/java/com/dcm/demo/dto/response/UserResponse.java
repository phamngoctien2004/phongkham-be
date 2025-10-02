package com.dcm.demo.dto.response;

import com.dcm.demo.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class UserResponse implements ProfileData {
    private Integer id;
    private String email;
    private String phone;
    private String name;
    private User.Role role;
    private Boolean status;
    private LocalDateTime createdAt;
    private DoctorResponse doctor;
}
