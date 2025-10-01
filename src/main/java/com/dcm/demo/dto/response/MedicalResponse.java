package com.dcm.demo.dto.response;

import com.dcm.demo.model.MedicalRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private String patientName;
    private LocalDateTime date;
    private MedicalRecord.RecordStatus status;

    private List<LabOrderResponse> labOrdersResponses;


}
