package com.dcm.demo.dto.response;

import com.dcm.demo.model.HealthPlan;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthPlanResponse {
    private int id;
    private String code;
    private String name;
    private Double price;
    private String description;
    private String roomNumber;
    private String roomName;
    private HealthPlan.ServiceType type;
    private List<DoctorResponse> doctorsAssigned;
    private List<HealthPlanResponse> subPlans;
}
