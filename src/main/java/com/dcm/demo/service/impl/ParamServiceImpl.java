package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.ParamDTO;
import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.dto.response.ParamResponse;
import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.mapper.ParamMapper;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.model.HealthPlanParam;
import com.dcm.demo.model.Param;
import com.dcm.demo.repository.ExaminationServiceRepository;
import com.dcm.demo.repository.LabOrderRepository;
import com.dcm.demo.repository.ParamRepository;
import com.dcm.demo.service.interfaces.ParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParamServiceImpl implements ParamService {
    private final ParamRepository repository;
    private final LabOrderRepository labOrderRepository;
    private final ExaminationServiceRepository examinationServiceRepository;
    private final HealthPlanParamRepository healthPlanParamRepository;
    private final ParamMapper mapper;
    @Override
    public List<ParamResponse> findAll(String keyword) {
        Specification<Param> spec = FilterHelper.contain(keyword, "name");
        return repository.findAll(spec).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void addParamInService(ParamDTO request) {
        isCUD(request.getHealthPlanId());
        HealthPlan healthPlan = examinationServiceRepository.findById(request.getHealthPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Health plan not found with id: " + request.getHealthPlanId()));
        List<HealthPlanParam> params = repository.findAllById(request.getRequestIds()).stream()
                .map(it -> {
                    HealthPlanParam param = new HealthPlanParam();
                    param.setParam(it);
                    param.setHealthPlan(healthPlan);
                    return param;
                }).toList();
        healthPlan.getHealthPlanParams().addAll(params);
        examinationServiceRepository.save(healthPlan);
    }


    @Override
    public void deleteParamInService(ParamDTO request) {
        isCUD(request.getHealthPlanId());
        List<Integer> healthPlanPramIds = request.getRequestIds();
        healthPlanParamRepository.deleteById(request.getHealthPlanId());
    }

    void isCUD(Integer healthPlanId) {
         if(labOrderRepository.existsByHealthPlanId(healthPlanId)) {
            throw new IllegalStateException("Cannot create, update or delete parameters for a health plan that has been used in lab orders.");
         }
    }
}
