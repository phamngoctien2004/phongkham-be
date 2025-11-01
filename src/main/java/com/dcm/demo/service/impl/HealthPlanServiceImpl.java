package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.HealthPlanRequest;
import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.dto.response.SlotResponse;
import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.mapper.HealthPlanMapper;
import com.dcm.demo.model.Department;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.model.Room;
import com.dcm.demo.model.healthPlanDetail;
import com.dcm.demo.repository.ExaminationServiceRepository;
import com.dcm.demo.service.interfaces.HealthPlanService;
import com.dcm.demo.service.interfaces.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthPlanServiceImpl implements HealthPlanService {
    private final ExaminationServiceRepository repository;
    private final HealthPlanMapper mapper;

    @Override
    @Transactional
    public List<HealthPlanResponse> getAllService(String keyword, HealthPlan.ServiceType type) {
        Specification<HealthPlan> spec = FilterHelper.contain(keyword, List.of("name"));
        if(type != null){
            spec = spec.and(FilterHelper.equal("type", type));
        }
        return repository.findAll(spec, Sort.by("code")).stream()
                .map(it -> {
                    HealthPlanResponse response = mapper.toResponse(it);
                    response.setRoomNumber(it.getRoom().getRoomNumber());
                    response.setRoomName(it.getRoom().getRoomName());
                    return response;
                })
                .toList();
    }

    @Override
    public Page<HealthPlanResponse> getAllService(String keyword, BigDecimal priceFrom, BigDecimal priceTo, HealthPlan.ServiceType type, Pageable pageable) {
        return repository.findAll(keyword, priceFrom, priceTo, type, pageable)
                .map(it -> {
                    HealthPlanResponse response = mapper.toResponse(it);
                    response.setRoomNumber(it.getRoom().getRoomNumber());
                    response.setRoomName(it.getRoom().getRoomName());
                    return response;
                });
    }

    @Override
    public HealthPlan findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Health plan not found"));
    }

    @Override
    public List<HealthPlanResponse> displayClientHealthPlans(HealthPlan.ServiceType type) {
        Specification<HealthPlan> spec = FilterHelper.equal("status", true);
        if(type != null){
            spec = spec.and(FilterHelper.equal("type", type));
        }
        return repository.findAll(spec, Sort.by("code")).stream()
                .map(it -> {;
                    HealthPlanResponse response = mapper.toResponse(it);
                    response.setRoomNumber(it.getRoom().getRoomNumber());
                    response.setRoomName(it.getRoom().getRoomName());
                    return response;
                })
                .toList();
    }

    @Override
    public HealthPlanResponse findResponseById(Integer id) {
        HealthPlan healthplan = findById(id);
        Department department = healthplan.getRoom().getDepartment();

        return mapper.toResponse(healthplan);
    }

    @Override
    public HealthPlanResponse findDetail(Integer id) {
        HealthPlan healthPlan = findById(id);
        HealthPlanResponse healthPlanResponse = mapper.toResponse(healthPlan);
        if(healthPlan.getType().equals(HealthPlan.ServiceType.DICH_VU)){
            List<HealthPlanResponse> subPlans = new ArrayList<>();
            List<healthPlanDetail> details = healthPlan.getHealthPlanDetails();
            for(healthPlanDetail detail : details){
                HealthPlan subPlan = detail.getServiceDetail();
                HealthPlanResponse subPlanResponse = mapper.toResponse(subPlan);
                Room room = subPlan.getRoom();
                subPlanResponse.setRoomName(room.getRoomName() + " - " + room.getRoomNumber());
                subPlans.add(subPlanResponse);
            }
            healthPlanResponse.setSubPlans(subPlans);
        }else{
            Room room = healthPlan.getRoom();
            healthPlanResponse.setRoomName(room.getRoomName() + " - " + room.getRoomNumber());
        }
        return healthPlanResponse;
    }

    @Override
    public HealthPlanResponse create(HealthPlanRequest request) {
        return null;
    }

    @Override
    public HealthPlanResponse update(HealthPlanRequest request) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }
}
