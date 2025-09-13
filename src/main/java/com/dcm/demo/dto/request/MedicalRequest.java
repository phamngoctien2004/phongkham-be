package com.dcm.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MedicalRequest {
    private Integer id;
    private Integer patientId;
    private Integer healthPlanId; //id goi kham
    private Integer doctorId;
    private String symptoms;
    private String clinicalExamination;
    private String diagnosis;
    private String treatmentPlan;
    private String note;
}
