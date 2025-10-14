package com.dcm.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpRequest {
    private String to;
    private String message;
}
