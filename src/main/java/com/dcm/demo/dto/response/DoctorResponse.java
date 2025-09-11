package com.dcm.demo.dto.response;

import com.dcm.demo.model.Degree;
import com.dcm.demo.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorResponse {
    private Integer id;
    private String fullName;
    private String phone;
    private String address;
    private LocalDate birth;
    private User.Gender gender;
    private String profileImage;
    private Degree degree;
    private Integer exp;
    private String position;
    private UserResponse user;
    private DepartmentResponse department;
}
