package com.dcm.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthPlanResponse {
    private int id;
    private String code;
    private String name;
    private double price;
    private String description;
    private String roomNumber;
    private String roomName;
}
