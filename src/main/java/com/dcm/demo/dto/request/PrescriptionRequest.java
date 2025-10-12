package com.dcm.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequest {
    private Integer id;
    private Integer medicalRecordId;
    private String generalInstructions;
}
