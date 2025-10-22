package com.dcm.demo.dto.response;

import com.dcm.demo.model.Appointment;
import com.dcm.demo.model.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentResponse {
    private Integer id;
    private PatientResponse patientResponse;
    private HealthPlanResponse healthPlanResponse;
    private DoctorResponse doctorResponse;
    private LocalDate date; // ngay kham
    private LocalTime time;
    private Appointment.AppointmentStatus status;
    private String symptoms;
    private String qr;
    private String invoiceCode;
    private BigDecimal totalAmount;
}
