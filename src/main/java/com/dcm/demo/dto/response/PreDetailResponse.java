package com.dcm.demo.dto.response;

import lombok.Data;

@Data
public class PreDetailResponse {
    private Integer id;
    private MedicineResponse medicineResponse;
    private String usageInstructions;
    private Integer quantity;
}
