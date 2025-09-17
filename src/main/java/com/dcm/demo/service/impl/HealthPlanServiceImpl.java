package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.mapper.HealthPlanMapper;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.repository.ExaminationServiceRepository;
import com.dcm.demo.service.interfaces.HealthPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class HealthPlanServiceImpl implements HealthPlanService {
    private final ExaminationServiceRepository repository;
    private final HealthPlanMapper mapper;
    @Override
    public List<HealthPlanResponse> getAllService() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public HealthPlan findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Health plan not found"));
    }
}
