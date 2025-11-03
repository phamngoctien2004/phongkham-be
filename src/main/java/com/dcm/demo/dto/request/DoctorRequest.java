package com.dcm.demo.dto.request;

import com.dcm.demo.model.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DoctorRequest {
    private Integer id;

    private String fullName;

    private String phone;

    private String address;

    private LocalDate birth;

    private User.Gender gender;

    private Integer departmentId;

    private Integer degreeId;

    private Integer exp;

    private String email;
}
