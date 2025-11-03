package com.dcm.demo.dto.request;

import com.dcm.demo.model.HealthPlan;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class HealthPlanRequest   {
    private Integer id;

    private String name;

    private HealthPlan.ServiceType type;

    private BigDecimal price;

    private String description;


    private Integer roomId;

    private List<Integer> detailIds;
    private List<Integer> paramIds;
}
