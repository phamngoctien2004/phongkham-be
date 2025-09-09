package com.dcm.demo.dto.request;

import com.dcm.demo.model.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private String fullName;
    private String phone;
    private User.Gender gender;
    private String email;
    private String address;
    private Integer examId; // neu chon goi kham thi id bac si va khoa se la null
    private Integer doctorId;
    private Integer departmentId;
    private LocalDate date; // ngay kham
    private LocalTime time;
    private String symptoms;
}
