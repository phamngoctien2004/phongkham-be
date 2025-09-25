package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.mapper.HealthPlanMapper;
import com.dcm.demo.mapper.LabOrderMapper;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.model.MedicalRecord;
import com.dcm.demo.repository.LabOrderRepository;
import com.dcm.demo.service.interfaces.HealthPlanService;
import com.dcm.demo.service.interfaces.LabOrderService;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabOrderServiceImpl implements LabOrderService {
    private final LabOrderRepository repository;
    private final UserService userService;
    private final HealthPlanService healthPlanService;
    private final HealthPlanMapper healthPlanMapper;
    private final MedicalRecordService medicalRecordService;
    private final LabOrderMapper mapper;

    @Override
    public List<LabOrderResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public LabOrderResponse findByRecordCode(String code) {
        return repository.findByMedicalRecordCode(code)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chỉ định"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<LabOrderResponse> createLabOrder(List<LabOrderRequest> requests) {
        List<LabOrderResponse> responses = new ArrayList<>();
        for(LabOrderRequest request : requests) {
            LabOrder labOrder = new LabOrder();

            MedicalRecord medicalRecord = medicalRecordService.findById(request.getRecordId());
            medicalRecord.setId(request.getRecordId());
            labOrder.setMedicalRecord(medicalRecord);

            HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
            if (healthPlan == null) {
                throw new RuntimeException("Dịch vụ không tồn tại");
            }
            labOrder.setHealthPlan(healthPlan);

            Doctor doctor = new Doctor();
            doctor.setId(request.getDoctorId());
            labOrder.setPerformingDoctor(doctor);

            labOrder.setPrice(healthPlan.getPrice());

//        khi nao mo security thi mo cai nay
//        User user = userService.getCurrentUser();
            labOrder.setOrderingDoctor(doctor);

            responses.add(buildResponse(repository.save(labOrder)));
        }
        return responses;
    }

    public LabOrderResponse buildResponse(LabOrder labOrder) {
        return LabOrderResponse.builder()
                .id(labOrder.getId())
                .recordId(labOrder.getMedicalRecord().getId())
                .healthPlanResponse(healthPlanMapper.toResponse(labOrder.getHealthPlan()))
                .price(labOrder.getPrice())
                .doctorOrdered(labOrder.getOrderingDoctor().getPosition())
                .doctorPerformed(labOrder.getPerformingDoctor().getPosition())
                .status(labOrder.getStatus())
                .createdAt(labOrder.getQueueTime())
                .expectedResultDate(labOrder.getExpectedResultDate())
                .build();
    }

    @Override
    public LabOrder findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chỉ định"));
    }
}
