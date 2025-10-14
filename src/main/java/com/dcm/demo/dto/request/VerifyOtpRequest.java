package com.dcm.demo.dto.request;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String phone;
    private Integer otp;
}
