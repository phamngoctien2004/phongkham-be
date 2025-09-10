package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.HealthPlanRequest;
import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.model.HealthPlan;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface HealthPlanMapper {
    HealthPlan toEntity(HealthPlanRequest userRequest);
    HealthPlanResponse toResponse(HealthPlan user);
}
