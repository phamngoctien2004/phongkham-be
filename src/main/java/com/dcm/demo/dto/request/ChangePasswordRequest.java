package com.dcm.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    private Integer id;
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
