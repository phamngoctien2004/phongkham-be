package com.dcm.demo.dto.response;

import lombok.Data;

@Data
public class MedicineResponse {
    private Integer id;
    private String name;
    private String concentration;
    private String dosageForm;
    private String description;
    private String unit;
}
