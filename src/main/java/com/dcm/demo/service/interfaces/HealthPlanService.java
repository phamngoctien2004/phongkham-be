package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.HealthPlanRequest;
import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.model.HealthPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface HealthPlanService {
    List<HealthPlanResponse> getAllService(String keyword, HealthPlan.ServiceType type);
    Page<HealthPlanResponse> getAllService(String keyword, BigDecimal priceFrom, BigDecimal priceTo,HealthPlan.ServiceType type, Pageable pageable);
    HealthPlan findById(Integer id);
    List<HealthPlanResponse> displayClientHealthPlans(HealthPlan.ServiceType type);
    HealthPlanResponse findResponseById(Integer id);
    HealthPlanResponse findDetail(Integer id);
    HealthPlanResponse create(HealthPlanRequest request);
    HealthPlanResponse update(HealthPlanRequest request);
    void delete(Integer id);
}
