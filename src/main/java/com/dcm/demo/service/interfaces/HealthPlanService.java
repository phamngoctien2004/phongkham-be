package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.model.HealthPlan;

import java.math.BigDecimal;
import java.util.List;

public interface HealthPlanService {
    List<HealthPlanResponse> getAllService(String keyword, HealthPlan.ServiceType type);
    HealthPlan findById(Integer id);
    List<HealthPlanResponse> displayClientHealthPlans();
    HealthPlanResponse findResponseById(Integer id);
    HealthPlanResponse findDetail(Integer id);
    List<HealthPlan> findAllById(List<Integer> ids);
    BigDecimal calcTotalService(List<Integer> ids);
}
