package com.dcm.demo.dto.request;

import com.dcm.demo.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    @NotBlank
    private String phone;
    @Email
    private String email;
    @NotBlank
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private User.Gender gender;

    @Min(value = 6)
    private String password;
    private String confirmPassword;
}
