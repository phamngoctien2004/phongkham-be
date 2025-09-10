package com.dcm.demo.dto.request;

import lombok.Data;

@Data
public class CreateMedicalRecord {
    private Integer appointmentId;
    private Integer doctorId;
    private String symptoms;
    private UserRequest patient;
}
