package com.dcm.demo.dto.request;

import com.dcm.demo.model.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private String fullName;
    private String phone;
    private User.Gender gender;
    private LocalDate birth;
    private String email;
    private String address;
    private Integer healthPlanId;
    private Integer doctorId;
    private Integer departmentId;
    private LocalDate date; // ngay kham
    private LocalTime time;
    private String symptoms;
}
