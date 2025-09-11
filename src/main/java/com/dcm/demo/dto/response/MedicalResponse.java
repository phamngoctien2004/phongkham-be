package com.dcm.demo.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicalResponse {
    private String id;
    private String code;
    private String symptoms;
    private String clinicalExamination;
    private String diagnosis;
    private String treatmentPlan;
    private String note;
    private BigDecimal total;
}
