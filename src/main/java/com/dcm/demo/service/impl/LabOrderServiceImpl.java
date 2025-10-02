package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.mapper.HealthPlanMapper;
import com.dcm.demo.mapper.LabOrderMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.LabOrderRepository;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabOrderServiceImpl implements LabOrderService {
    private final LabOrderRepository repository;
    private final UserService userService;
    private final HealthPlanService healthPlanService;
    private final HealthPlanMapper healthPlanMapper;
    private final MedicalRecordService medicalRecordService;
    private final DoctorService doctorService;
    private final LabOrderMapper mapper;
    private final InvoiceService invoiceService;

    @Override
    public List<LabOrderResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public LabOrderResponse findByRecordCode(String code) {
        return repository.findByMedicalRecordCode(code)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chỉ định"));
    }


    public LabOrderResponse buildResponse(LabOrder labOrder) {
        HealthPlan healthPlan = labOrder.getHealthPlan();
        return LabOrderResponse.builder()
                .id(labOrder.getId())
                .recordId(labOrder.getMedicalRecord().getId())
                .healthPlanId(healthPlan.getId())
                .healthPlanName(healthPlan.getName())
                .price(labOrder.getPrice())
                .doctorOrdered(labOrder.getOrderingDoctor() != null ? labOrder.getOrderingDoctor().getPosition() : null)
                .doctorPerformed(labOrder.getPerformingDoctor() != null ? labOrder.getPerformingDoctor().getPosition() : null)
                .room(labOrder.getRoom())
                .status(labOrder.getStatus())
                .diagnosis(labOrder.getDiagnosis())
                .orderDate(labOrder.getOrderDate())
                .expectedResultDate(labOrder.getExpectedResultDate())
                .build();
    }

    @Override
    public LabOrder findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chỉ định2"));
    }

    @Override
    public LabOrderResponse findResponseById(Integer id) {
        return buildResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chỉ định3")));
    }

    @Override
    public void createLabOrderFromHealthPlan(MedicalRecord medicalRecord, Integer healthPlanId) {
        if (healthPlanId == null) {
            HealthPlan healthPlan = healthPlanService.findById(1);
            buildEntity(medicalRecord, medicalRecord.getDoctor(), healthPlan, medicalRecord.getFee(), null);
            return;
        }
        HealthPlan healthPlan = healthPlanService.findById(healthPlanId);
        List<healthPlanDetail> healthPlanDetails = healthPlan.getHealthPlanDetails();

        if (healthPlanDetails != null && !healthPlanDetails.isEmpty()) {
            HealthPlan temp = healthPlanService.findById(1);

            buildEntity(medicalRecord, medicalRecord.getDoctor(), temp, BigDecimal.ZERO, null);
            healthPlanDetails.forEach(detail -> {
                buildEntity(medicalRecord, null, detail.getServiceDetail(), detail.getServiceDetail().getPrice(), null);
            });
            return;
        }
        buildEntity(medicalRecord, null, healthPlan, healthPlan.getPrice(), null);
    }

    @Override
    @Transactional
    public void createLabOrder(LabOrderRequest request) {
        MedicalRecord medicalRecord = medicalRecordService.findById(request.getRecordId());
        BigDecimal total = BigDecimal.ZERO;
        HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
        Doctor doctor = doctorService.findById(request.getPerformingDoctorId());
        total = total.add(healthPlan.getPrice());
        buildEntity(medicalRecord, doctor, healthPlan, healthPlan.getPrice(), request.getDiagnosis());
        medicalRecordService.updateTotal(medicalRecord, total);
    }

    @Override
    public void updateLabOrder(LabOrderRequest request) {
        LabOrder labOrder = findById(request.getId());
        Doctor doctor = new Doctor();
        doctor.setId(request.getPerformingDoctorId());
        labOrder.setPerformingDoctor(doctor);
        repository.save(labOrder);
    }

    private void buildEntity(MedicalRecord medicalRecord, Doctor doctor, HealthPlan healthPlan, BigDecimal price, String diagnosis) {
        LabOrder labOrder = new LabOrder();
        labOrder.setMedicalRecord(medicalRecord);
        labOrder.setHealthPlan(healthPlan);
        labOrder.setOrderingDoctor(medicalRecord.getDoctor());
        labOrder.setPerformingDoctor(doctor);
        labOrder.setPrice(price);
        labOrder.setDiagnosis(diagnosis);
        Room room = healthPlan.getRoom();
        labOrder.setRoom(room.getRoomName() + " - " + room.getRoomNumber());
        repository.save(labOrder);
    }
}
