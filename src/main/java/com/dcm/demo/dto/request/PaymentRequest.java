package com.dcm.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PaymentRequest {
    private Integer medicalRecordId;
    private List<Integer> healthPlanIds;
    private List<Integer> labOrderIds;
    private Integer doctorId;
    private String totalAmount;
}
