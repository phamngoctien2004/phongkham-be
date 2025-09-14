package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.model.HealthPlan;

import java.util.List;

public interface HealthPlanService {
    List<HealthPlanResponse> getAllService();
    HealthPlan findById(Integer id);
}
