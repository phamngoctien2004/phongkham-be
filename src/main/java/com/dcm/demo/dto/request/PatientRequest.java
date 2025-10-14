package com.dcm.demo.dto.request;

import com.dcm.demo.model.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PatientRequest {
    private Integer id;
    private String phone;
    private String email;
    private String fullName;
    private String address;
    private String cccd;
    private LocalDate birth;
    private User.Gender gender;
    private String bloodType;
    private BigDecimal weight;
    private BigDecimal height;
    private String profileImage;
//  lien ket tai khoan

    private String  phoneLink;
    private String relationshipName;
    private String sync;
}
