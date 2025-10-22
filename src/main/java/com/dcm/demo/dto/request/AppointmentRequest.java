package com.dcm.demo.dto.request;

import com.dcm.demo.model.Appointment;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private Integer id;
    private Integer healthPlanId;
    private Integer doctorId;
    private Integer patientId;
    private LocalDate date; // ngay kham
    private LocalTime time;
    private String symptoms;
    private Appointment.AppointmentStatus status;
}
