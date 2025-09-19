package com.dcm.demo.dto.request;

import com.dcm.demo.model.Appointment;
import com.dcm.demo.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private Integer id;
    private Integer healthPlanId;
    private Integer doctorId;
    private Integer departmentId;
    private Integer patientId;
    private String fullName;
    private String phone;
    private User.Gender gender;
    private LocalDate birth;
    private String email;
    private String address;

    private LocalDate date; // ngay kham
    private LocalTime time;
    private String symptoms;
    private Appointment.AppointmentStatus status;
}
