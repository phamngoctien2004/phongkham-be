package com.dcm.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PaymentRequest {
    private Integer medicalRecordId;
    private List<Integer> healthPlanIds;
    private Integer doctorId;
    private Integer totalAmount;
}
