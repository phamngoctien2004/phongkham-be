package com.dcm.demo.dto.request;

import com.dcm.demo.model.LabOrder;
import lombok.Data;

@Data
public class LabOrderRequest {
    private Integer id;
    private Integer recordId;
    private Integer healthPlanId;
    private Integer performingDoctorId;
    private String diagnosis;
    private LabOrder.TestStatus status;
}
