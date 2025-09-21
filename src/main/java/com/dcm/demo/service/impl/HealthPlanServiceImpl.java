package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.mapper.HealthPlanMapper;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.repository.ExaminationServiceRepository;
import com.dcm.demo.service.interfaces.HealthPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
public class HealthPlanServiceImpl implements HealthPlanService {
    private final ExaminationServiceRepository repository;
    private final HealthPlanMapper mapper;

    @Override
    @Transactional
    public List<HealthPlanResponse> getAllService() {
        return repository.findAll().stream()
                .map(it -> {
                    HealthPlanResponse response = mapper.toResponse(it);
                    response.setRoomNumber(it.getRoom().getRoomNumber());
                    response.setRoomName(it.getRoom().getRoomName());
                    return response;
                })
                .toList();
    }

    @Override
    public HealthPlan findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Health plan not found"));
    }

    @Override
    public List<HealthPlan> findAllById(List<Integer> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public BigDecimal calcTotalService(List<Integer> ids) {
        List<HealthPlan> healthPlans = repository.findAllById(ids);
        BigDecimal total = BigDecimal.ZERO;
        for (HealthPlan healthPlan : healthPlans) {
            total = total.add(healthPlan.getPrice());
        }
        return total;
    }
}
