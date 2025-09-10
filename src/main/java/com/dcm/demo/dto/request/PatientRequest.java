package com.dcm.demo.dto.request;

import com.dcm.demo.model.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PatientRequest {
    private int id;
    private String fullName;
    private String address;
    private String cccd;
    private LocalDate birth;
    private User.Gender gender;
    private String bloodType;
    private BigDecimal weight;
    private BigDecimal height;
    private String profileImage;
}
