package com.dcm.demo.dto.request;

import lombok.Data;

@Data
public class OtpRequest {
    private String to;
    private String message;
}
