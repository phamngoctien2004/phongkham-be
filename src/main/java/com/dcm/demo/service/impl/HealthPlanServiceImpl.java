package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.HealthPlanRequest;
import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.dto.response.ParamResponse;
import com.dcm.demo.dto.response.SlotResponse;
import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.mapper.HealthPlanMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.ExaminationServiceRepository;
import com.dcm.demo.repository.ParamRepository;
import com.dcm.demo.repository.RoomRepository;
import com.dcm.demo.service.interfaces.HealthPlanService;
import com.dcm.demo.service.interfaces.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthPlanServiceImpl implements HealthPlanService {
    private final ExaminationServiceRepository repository;
    private final HealthPlanMapper mapper;
    private final RoomRepository roomRepository;
    private final ParamRepository paramRepository;
    @Override
    @Transactional
    @Cacheable(
            value = "healthPlans",
            key = "'all'",
            condition = "(#keyword == null or #keyword.isBlank()) and #type == null"
    )
    public List<HealthPlanResponse> getAllService(String keyword, HealthPlan.ServiceType type) {
        log.info("Get all Health Plans db");
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
    @Cacheable(value = "healthPlans2", key = "#id")
    public HealthPlanResponse findResponseById(Integer id) {
        log.info("Get Health Plan by id from db: {}", id);
        HealthPlan healthplan = findById(id);
        return mapper.toResponse(healthplan);
    }

    @Override
    @Cacheable(value = "healthPlans", key = "#id")
    public HealthPlanResponse findDetail(Integer id) {
        HealthPlan healthPlan = findById(id);
        return buildResponse(healthPlan);
    }

    @Override
    public List<ParamResponse> findParamsByServiceId(Integer serviceId) {
        HealthPlan healthPlan = findById(serviceId);
        if(healthPlan.getHealthPlanParams() != null && !healthPlan.getHealthPlanParams().isEmpty()) {
            return healthPlan.getHealthPlanParams().stream()
                    .map(it -> {
                        Param param = it.getParam();
                        ParamResponse response = new ParamResponse();
                        response.setId(param.getId());
                        response.setName(param.getName());
                        response.setUnit(param.getUnit());
                        response.setRange(param.getRange());
                        return response;
                    })
                    .toList();
        }
        return null;
    }

    HealthPlanResponse buildResponse(HealthPlan healthPlan){
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
    @Transactional(rollbackFor =  Exception.class)
    @CacheEvict(value = "healthPlans", key = "'all'")
    @CachePut(value = "healthPlans", key = "#result.id")
    public HealthPlanResponse create(HealthPlanRequest request) {
        HealthPlan healthPlan = mapper.toEntity(request);
        healthPlan.setCode("HP-" + System.currentTimeMillis());
        if(request.getRoomId() != null){
            Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));
            healthPlan.setRoom(room);
        }
        if(request.getDetailIds() != null && !request.getDetailIds().isEmpty()){
            List<healthPlanDetail> details = repository.findAllById(request.getDetailIds()).stream()
                    .map(it -> {
                        healthPlanDetail detail = new healthPlanDetail();
                        detail.setServiceDetail(it);
                        detail.setService(healthPlan);
                        return detail;
                    })
                    .toList();
            healthPlan.setHealthPlanDetails(details);
        }
        if(request.getParamIds() != null && !request.getParamIds().isEmpty()){
            List<HealthPlanParam> params = paramRepository.findAllById(request.getParamIds()).stream()
                    .map(it -> {
                        HealthPlanParam param = new HealthPlanParam();
                        param.setParam(it);
                        param.setHealthPlan(healthPlan);
                        return param;
                    }).toList();
            healthPlan.setHealthPlanParams(params);
        }
        return this.buildResponse(repository.save(healthPlan));
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    @CacheEvict(value = "healthPlans", key = "'all'")
    @CachePut(value = "healthPlans", key = "#result.id")
    public HealthPlanResponse update(HealthPlanRequest request) {
        HealthPlan healthPlan = findById(request.getId());
        healthPlan.setName(request.getName());
        healthPlan.setPrice(request.getPrice());
        healthPlan.setDescription(request.getDescription());

        if(request.getRoomId() != null){
            Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));
            healthPlan.setRoom(room);
        }
        return this.buildResponse(repository.save(healthPlan));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
