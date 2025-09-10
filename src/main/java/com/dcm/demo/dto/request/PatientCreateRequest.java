package com.dcm.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PatientCreateRequest {
    UserRequest user;
    private String bloodType;
    private BigDecimal weight;
    private BigDecimal height;
}
