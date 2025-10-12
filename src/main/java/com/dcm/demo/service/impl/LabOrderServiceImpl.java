package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.LabDeleteRequest;
import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.dto.response.LabResultResponse;
import com.dcm.demo.mapper.HealthPlanMapper;
import com.dcm.demo.mapper.LabOrderMapper;
import com.dcm.demo.mapper.LabResultMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.InvoiceDetailRepository;
import com.dcm.demo.repository.LabOrderRepository;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final LabResultMapper labResultMapper;
    private final InvoiceService invoiceService;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final DepartmentService departmentService;
    private BigDecimal defaultPrice = BigDecimal.valueOf(2000);
    @Override
    public List<LabOrderResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public List<LabOrderResponse> getByDoctorPerforming(String keyword, LocalDate date, LabOrder.TestStatus status) {
        User user = userService.getCurrentUser();
        Integer doctorId = user.getDoctor() != null ? user.getDoctor().getId() : 0;

        LocalDateTime from = null, to = null;
        if (date != null) {
            from = date.atStartOfDay();
            to = date.plusDays(1).atStartOfDay();
        }

        return repository.findAll(doctorId, keyword, from, to, status)
                .stream()
                .map(this::buildResponse)
                .toList(
        );
    }

    @Override
    public List<LabOrder> findByIds(List<Integer> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public LabOrderResponse findByRecordCode(String code) {
        return repository.findByMedicalRecordCode(code)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chỉ định"));
    }


    public LabOrderResponse buildResponse(LabOrder labOrder) {
        HealthPlan healthPlan = labOrder.getHealthPlan();
        LabResultResponse labOrderResult = labResultMapper.toResponse(labOrder.getLabResult());
        return LabOrderResponse.builder()
                .id(labOrder.getId())
                .code(labOrder.getCode())
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
                .labResultResponse(labOrderResult)
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
//      kham bac si chuyen khoa
        HealthPlan healthPlan = healthPlanService.findById(1);
        if (healthPlanId == null) {
            buildEntity(medicalRecord, medicalRecord.getDoctor(), healthPlan, null,medicalRecord.getFee(), null);
            return;
        }

        HealthPlan healthPlanPrimary = healthPlanService.findById(healthPlanId);
//      chi dinh le
        if(!healthPlanPrimary.getType().equals(HealthPlan.ServiceType.DICH_VU)){
            buildEntity(medicalRecord, medicalRecord.getDoctor(), healthPlan, null,defaultPrice, null);
            buildEntity(medicalRecord, null, healthPlanPrimary, null,healthPlanPrimary.getPrice(), null);
            return;
        }

//      goi kham
        List<healthPlanDetail> healthPlanDetails = healthPlanPrimary.getHealthPlanDetails();
        if (healthPlanDetails != null && !healthPlanDetails.isEmpty()) {
            healthPlanDetails.forEach(detail -> {
                buildEntity(medicalRecord, null, detail.getServiceDetail(), detail.getService(), BigDecimal.ZERO, null);
            });
        }

    }

    @Override
    @Transactional
    public void deleteAllById(LabDeleteRequest request) {
//     logic check neeu da co chi dinh xn hoac da thanh toan thi k xoa duoc

//      xoa hoa don lien quan
        MedicalRecord record = medicalRecordService.findById(request.getMedicalRecordId());
        Invoice invoice = record.getInvoice();
        List<LabOrder> labOrders = repository.findAllById(request.getIds());
        labOrders.forEach(labOrder -> {
            invoiceDetailRepository.deleteByInvoiceIdAndHealthPlanId(invoice.getId(), labOrder.getHealthPlan().getId());
            repository.deleteById(labOrder.getId());
        });
    }

    @Override
    @Transactional
    public void createLabOrder(LabOrderRequest request) {
        MedicalRecord medicalRecord = medicalRecordService.findById(request.getRecordId());
        HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
        Doctor doctor = doctorService.findById(request.getPerformingDoctorId());
        buildEntity(medicalRecord, doctor, healthPlan, null,healthPlan.getPrice(), request.getDiagnosis());

        Invoice invoice = medicalRecord.getInvoice();
        invoiceService.buildInvoiceDetail(
                invoice,
                healthPlan.getId(),
                healthPlan.getPrice(),
                InvoiceDetail.Status.CHUA_THANH_TOAN,
                Invoice.PaymentMethod.TIEN_MAT,
                BigDecimal.ZERO
        );
        invoiceService.updateTotal(invoice, healthPlan.getPrice());
    }

    @Override
    public void updateLabOrder(LabOrderRequest request) {
        LabOrder labOrder = findById(request.getId());
        Doctor doctor = new Doctor();
        doctor.setId(request.getPerformingDoctorId());
        labOrder.setPerformingDoctor(doctor);
        repository.save(labOrder);
    }

    @Override
    public void updateStatus(Integer id, LabOrder.TestStatus status) {
        LabOrder labOrder = findById(id);
        labOrder.setStatus(status);
        repository.save(labOrder);
    }

    private void buildEntity(MedicalRecord medicalRecord, Doctor doctor, HealthPlan healthPlan, HealthPlan parent, BigDecimal price, String diagnosis) {
        LabOrder labOrder = new LabOrder();
        labOrder.setMedicalRecord(medicalRecord);
        labOrder.setCode("XN" + System.currentTimeMillis());
        labOrder.setHealthPlan(healthPlan);
        labOrder.setOrderingDoctor(medicalRecord.getDoctor());
        labOrder.setPerformingDoctor(doctor);
        labOrder.setPrice(price);
        labOrder.setDiagnosis(diagnosis);
//        labOrder.setHealthPlanParent(parent);
        if(doctor != null) {
            Department department = doctor.getDepartment();
            labOrder.setRoom(departmentService.getRoomFromDepartment(department));
        }else{
            Room room = healthPlan.getRoom();
            labOrder.setRoom(room.getRoomName() + " - " + room.getRoomNumber());
        }
        if(healthPlan.getId() == 1) {
            labOrder.setStatus(LabOrder.TestStatus.HOAN_THANH);
            labOrder.setOrderDate(LocalDateTime.now().minusHours(1));
            labOrder.setExpectedResultDate(LocalDateTime.now());
        }
        repository.save(labOrder);
    }
}
