package com.dcm.demo.dto.response;

import com.dcm.demo.model.Degree;
import com.dcm.demo.model.Schedule;
import com.dcm.demo.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class DoctorResponse implements  ProfileData{
    private Integer id;
    private String fullName;
    private String phone;
    private String address;
    private LocalDate birth;
    private User.Gender gender;
    private String profileImage;
    private Degree degreeResponse;
    private Integer exp;
    private String position;
    private UserResponse userResponse;
    private DepartmentResponse departmentResponse;
    private BigDecimal examinationFee;
    private boolean available = true;
    private Schedule.Shift shift;
    private String roomNumber;
    private String roomName;
}
