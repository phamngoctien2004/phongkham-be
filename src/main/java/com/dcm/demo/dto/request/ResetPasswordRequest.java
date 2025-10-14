package com.dcm.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    private Integer userId;

    @Min(value = 6)
    private String password;
    private String confirmPassword;
}
