package com.dcm.demo.dto.response;

import com.dcm.demo.model.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentResponse {
    private Integer id;
    private String fullName;
    private String phone;
    private User.Gender gender;
    private LocalDate birth;
    private String email;
    private String address;
    private HealthPlanResponse healthPlanResponse;
    private DoctorResponse doctorResponse;
    private DepartmentResponse departmentResponse;
    private LocalDate date; // ngay kham
    private LocalTime time;
    private String symptoms;
}
