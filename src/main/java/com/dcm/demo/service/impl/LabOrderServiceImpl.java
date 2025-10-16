package com.dcm.demo.service.impl;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
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
    public Page<LabOrderResponse> getByDoctorPerforming(String keyword, LocalDate date, LabOrder.TestStatus status, Pageable pageable) {
        User user = userService.getCurrentUser();
        Department department = user.getDoctor().getDepartment();

        LocalDateTime from = null, to = null;
        if (date != null) {
            from = date.atStartOfDay();
            to = date.plusDays(1).atStartOfDay();
        }

        return repository.findAllWithPagination(department.getId(), keyword, from, to, status, pageable)
                .map(this::buildResponse);
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
    public LabOrderResponse fetchAndMarkProcessingLabOrder(Integer id) {
        LabOrder labOrder = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chỉ định"));

//      lay user hien tai muon truy cap
        User user = userService.getCurrentUser();

        if (labOrder.getStatus().equals(LabOrder.TestStatus.DANG_THUC_HIEN)
                && !Objects.equals(labOrder.getPerformingDoctor().getId(), user.getDoctor().getId())) {
            throw new RuntimeException("Phiếu chỉ định đang được thực hiện bởi bac si " + labOrder.getPerformingDoctor().getFullName());
        }
        if (labOrder.getStatus().equals(LabOrder.TestStatus.CHO_THUC_HIEN)) {
            labOrder.setStatus(LabOrder.TestStatus.DANG_THUC_HIEN);
            labOrder.setPerformingDoctor(user.getDoctor());
            labOrder.setOrderDate(LocalDateTime.now());
            labOrder.setExpectedResultDate(LocalDateTime.now().plusHours(1));
            repository.save(labOrder);
            return buildResponse(labOrder);
        }
        return buildResponse(labOrder);
    }

    @Override
    public void createLabOrderFromHealthPlan(MedicalRecord medicalRecord, Integer healthPlanId) {
        log.info("Create lab order from health plan id: {}", healthPlanId);
//      kham cu the bac si
        HealthPlan healthPlan = healthPlanService.findById(1);
        if (healthPlanId == null || healthPlanId == 0) {
            buildEntity(medicalRecord, medicalRecord.getDoctor(), healthPlan, null, medicalRecord.getFee(), null);
            return;
        }

        HealthPlan healthPlanPrimary = healthPlanService.findById(healthPlanId);
//      kham chi dinh / chuyen khoa (khong can chon bac si)
        if (!healthPlanPrimary.getType().equals(HealthPlan.ServiceType.DICH_VU)) {
            if (!healthPlanPrimary.getType().equals(HealthPlan.ServiceType.CHUYEN_KHOA)) {
                buildEntity(medicalRecord, null, healthPlan, null, BigDecimal.ZERO, null);
            }
            log.info("Create cdxn: {}", healthPlanId);
            buildEntity(medicalRecord, null, healthPlanPrimary, null, healthPlanPrimary.getPrice(), null);
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
    public void delete(Integer id) {
        LabOrder labOrder = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chỉ định4"));
//      xoa hoa don lien quan
        MedicalRecord record = labOrder.getMedicalRecord();
        Invoice invoice = record.getInvoice();
        HealthPlan combo = null;
        for (InvoiceDetail detail : invoice.getInvoiceDetails()) {
            if (Objects.equals(detail.getHealthPlan().getId(), labOrder.getHealthPlan().getId())) {
                if (detail.getStatus() == InvoiceDetail.Status.DA_THANH_TOAN) {
                    throw new RuntimeException("Không thể xóa phiếu chỉ định đã thanh toán");
                }
            }
            if (detail.getHealthPlan().getType().equals(HealthPlan.ServiceType.DICH_VU)) {
                combo = detail.getHealthPlan();
            }
        }

        if (combo != null) {
            combo.getHealthPlanDetails().forEach(detail -> {
                if (Objects.equals(detail.getServiceDetail().getId(), labOrder.getMedicalRecord().getId())) {
                    throw new RuntimeException("Không thể xóa phiếu chỉ định thuộc gói khám");
                }
            });
        }

        invoice.getInvoiceDetails().removeIf(detail ->
                detail.getHealthPlan().getId().equals(labOrder.getHealthPlan().getId()));
        repository.deleteById(labOrder.getId());
        invoiceService.updateTotal(invoice, labOrder.getPrice().negate());
    }

    @Override
    @Transactional
    public void createLabOrder(LabOrderRequest request) {
        MedicalRecord medicalRecord = medicalRecordService.findById(request.getRecordId());
        HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());

        if (request.getPerformingDoctorId() != null) {
            Doctor doctor = doctorService.findById(request.getPerformingDoctorId());
            buildEntity(medicalRecord, doctor, healthPlan, null, healthPlan.getPrice(), request.getDiagnosis());
        } else {
            buildEntity(medicalRecord, null, healthPlan, null, healthPlan.getPrice(), request.getDiagnosis());
        }

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
        if (request.getPerformingDoctorId() != null) {
            Doctor doctor = new Doctor();
            doctor.setId(request.getPerformingDoctorId());
            labOrder.setPerformingDoctor(doctor);
        }

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
        if (doctor != null) {
            Department department = doctor.getDepartment();
            labOrder.setRoom(departmentService.getRoomFromDepartment(department));
        } else {
            Room room = healthPlan.getRoom();
            labOrder.setRoom(room.getRoomName() + " - " + room.getRoomNumber());
        }
        if (healthPlan.getId() == 1) {
            labOrder.setStatus(LabOrder.TestStatus.HOAN_THANH);
            labOrder.setOrderDate(LocalDateTime.now().minusHours(1));
            labOrder.setExpectedResultDate(LocalDateTime.now());
        }
        repository.save(labOrder);
    }
}
