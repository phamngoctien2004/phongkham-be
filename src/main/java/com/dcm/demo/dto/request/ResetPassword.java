package com.dcm.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPassword {
    @Email
    private String email;
    @NotBlank
    private String tokenReset;
    @Size(min = 6, max = 20)
    private String newPassword;
    private String confirmNewPassword;
}
