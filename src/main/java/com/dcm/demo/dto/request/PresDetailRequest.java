package com.dcm.demo.dto.request;

import lombok.Data;

@Data
public class PresDetailRequest {
    private Integer id;
    private Integer medicineId;
    private Integer prescriptionId;
    private String usageInstructions;
    private Integer quantity;
}
