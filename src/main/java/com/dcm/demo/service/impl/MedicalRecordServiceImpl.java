package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.dto.response.MedicalResponse;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.LabOrderMapper;
import com.dcm.demo.mapper.MedicalMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.InvoiceDetailRepository;
import com.dcm.demo.repository.MedicalRecordRepository;
import com.dcm.demo.repository.RelationshipRepository;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordRepository repository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final UserService userService;
    private final RelationshipRepository relationshipRepository;
    private final HealthPlanService healthPlanService;
    private final MedicalMapper mapper;
    private final LabOrderMapper labOrderMapper;
    private final FileService fileService;
    private final InvoiceService invoiceService;
    private final InvoiceDetailRepository invoiceDetailRepository;
    @Override
    public byte[] exportPdf(Integer id) {
        MedicalRecord medicalRecord = repository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOTFOUND)
        );
        Patient patient = medicalRecord.getPatient();
        HealthPlan healthPlan = medicalRecord.getHealthPlan();
        Doctor doctor = medicalRecord.getDoctor();
        String healName = healthPlan != null ? healthPlan.getName() : "Goi kham thuong";
        BigDecimal healPrice = healthPlan != null ? healthPlan.getPrice() : doctor.getDegree().getExaminationFee();
        LocalDate now = LocalDate.now();
        Map<String, Object> params = Map.of(
                "tenBenhNhan", patient.getFullName(),
                "diaChi", patient.getAddress(),
                "thanhToan", "Tien Mat",
                "total", medicalRecord.getTotal(),
                "today", now
        );
        List<Map<String, Object>> items = List.of(
                Map.of("name", healName, "price", healPrice)
        );

        return fileService.render("pdfs/invoice", params, items);
    }

    @Override
    @Transactional
    public MedicalRecord create(MedicalRequest request) {
        MedicalRecord medicalRecord = new MedicalRecord();

        Patient patient = new Patient();
        patient.setId(request.getPatientId());
        medicalRecord.setPatient(patient);

        medicalRecord.setCode("PK" + System.currentTimeMillis() / 1000);
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setClinicalExamination(request.getClinicalExamination());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTreatmentPlan(request.getTreatmentPlan());
        medicalRecord.setNote(request.getNote());
        BigDecimal fee = BigDecimal.ZERO;

//        tao healthplan phi kham benh
        if (request.getDoctorId() != null) {
            Doctor doctor = doctorService.findById(request.getDoctorId());
            medicalRecord.setDoctor(doctor);
            fee = fee.add(doctor.getDegree().getExaminationFee());
            HealthPlan healthPlan = new HealthPlan();
            healthPlan.setId(1);
            medicalRecord.setHealthPlan(healthPlan);
            medicalRecord.setFee(fee);
            medicalRecord.setTotal(fee);
        }

//      khong tao health plan phi kham benh
        if (request.getHealthPlanId() != null) {
            HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
            medicalRecord.setHealthPlan(healthPlan);
            medicalRecord.setTotal(fee.add(healthPlan.getPrice()));
            Doctor doctor = new Doctor();
            doctor.setId(1);
            medicalRecord.setDoctor(doctor);
        }
        MedicalRecord saved = repository.save(medicalRecord);
        if(request.getInvoiceId() != null) {
            Invoice invoice = invoiceService.findById(request.getInvoiceId());
            invoice.setMedicalRecord(saved);
            invoiceService.save(invoice);
        }else{
            invoiceService.createDetailInvoice(saved);
        }
        return saved;
    }

    @Override
    public void update(MedicalRequest request) {
        MedicalRecord medicalRecord = repository.findById(request.getId()).orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOTFOUND)
        );

        Doctor doctor = new Doctor();
        doctor.setId(request.getDoctorId());
        medicalRecord.setDoctor(doctor);
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setClinicalExamination(request.getClinicalExamination());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTreatmentPlan(request.getTreatmentPlan());
        medicalRecord.setNote(request.getNote());
        repository.save(medicalRecord);
    }

    @Override
    public List<MedicalResponse> getRelationMedicalRecord(String cccd) {
        User user = userService.getCurrentUser();
        Patient patient = patientService.findByCccd(cccd);
//        check relationship
        relationshipRepository.findByPatientIdAndUserId(patient.getId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Not found relationship"));

        return repository.findByPatientIdOrderByDateDesc(patient.getId()).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void updateTotal(MedicalRecord medicalRecord, BigDecimal total) {
        BigDecimal currentTotal = medicalRecord.getTotal();
        medicalRecord.setTotal(currentTotal.add(total));
        repository.save(medicalRecord);
    }

    @Override
    public List<MedicalResponse> me() {
        PatientResponse currentPatient = patientService.me();
        return repository.findByPatientIdOrderByDateDesc(currentPatient.getId()).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public MedicalRecord findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Medical record not found"));
    }

    @Override
    public MedicalResponse getDetailById(Integer id) {
        return buildResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Medical record not found")));
    }

    @Override
    public List<MedicalResponse> findAll(String keyword, MedicalRecord.RecordStatus status, LocalDate date) {
        LocalDateTime from = null, to = null;
        if (date != null) {
            from = date.atStartOfDay();
            to = date.plusDays(1).atStartOfDay();
        }
        return repository.findAll(keyword, from, to, status).stream()
                .map(this::buildResponse)
                .toList();
    }

    private MedicalResponse buildResponse(MedicalRecord record) {
        MedicalResponse response = mapper.toResponse(record);
        response.setPatientName(record.getPatient().getFullName());

        List<LabOrderResponse> services = new ArrayList<>();
        List<LabOrder> labOrders = record.getLabOrders();
        if(labOrders != null){
            labOrders.forEach(labOrder -> {
                HealthPlan healthPlan = labOrder.getHealthPlan();
                LabOrderResponse detail = labOrderMapper.toResponse(labOrder);
                detail.setHealthPlanId(healthPlan.getId());
                detail.setHealthPlanName(healthPlan.getName());
                if(labOrder.getPerformingDoctor() != null){
                    detail.setDoctorPerformed(labOrder.getPerformingDoctor().getFullName());
                    detail.setDoctorPerformedId(labOrder.getPerformingDoctor().getId());
                }
                if(labOrder.getOrderingDoctor() != null){
                    detail.setDoctorOrdered(labOrder.getOrderingDoctor().getFullName());
                }
                List<Integer> healthPlanUnPay = invoiceDetailRepository.findServicesUnpay(record.getId());
                if(healthPlanUnPay.contains(healthPlan.getId())){
                    detail.setStatusPayment("CHUA_THANH_TOAN");
                } else {
                    detail.setStatusPayment("DA_THANH_TOAN");
                }
                services.add(detail);
            });
        }
        response.setLabOrdersResponses(services);
        return response;
    }

//    check da thu tien chua
}
