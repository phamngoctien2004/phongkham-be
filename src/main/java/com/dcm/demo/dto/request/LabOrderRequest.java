package com.dcm.demo.dto.request;

import lombok.Data;

@Data
public class LabOrderRequest {
    private Integer id;
    private Integer recordId;
    private Integer healthPlanId;
    private Integer doctorId;
}
