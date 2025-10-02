package com.dcm.demo.dto.response;

import com.dcm.demo.model.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public final class PatientResponse implements ProfileData{
    private Integer id;
    private String code;
    private String bloodType;
    private BigDecimal weight;
    private BigDecimal height;
    private LocalDateTime registrationDate;
    private String fullName;
    private String phone;
    private String address;
    private String cccd;
    private LocalDate birth;
    private User.Gender gender;
    private String profileImage;
    private String relationship;
    private String email;
}
