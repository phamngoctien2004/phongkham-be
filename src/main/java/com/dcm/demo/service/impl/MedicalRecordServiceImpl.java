package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.request.WebhookRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
    private final RedisTemplate<String, Object> redisTemplate;

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
        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceService.findById(request.getInvoiceId());
            invoice.setMedicalRecord(saved);
            invoiceService.save(invoice);
        } else {
            invoiceService.createInvoiceForCash(saved);
        }
        return saved;
    }


    //    thanh toan tien mat tu lan 2 tro di
    @Override
    public void updatePaymentForLabOrder(MedicalRequest.UpdatePaymentRequest request) {
        MedicalRecord medicalRecord = findById(request.getMedicalRecordId());
        List<Integer> labOrderIds = request.getLabOrderIds();
//      filter laborder duoc chon tu phieu kham
        List<LabOrder> labOrders = medicalRecord.getLabOrders().stream()
                .filter(it -> labOrderIds.contains(it.getId()))
                .toList();
//      tao chi tiet hoa don
        Invoice invoice = medicalRecord.getInvoice();
        if (invoice != null) {
            BigDecimal amount = BigDecimal.ZERO;
            for (LabOrder labOrder : labOrders) {
                invoiceService.buildInvoiceDetail(
                        invoice,
                        labOrder.getHealthPlan().getId(),
                        labOrder.getPrice(),
                        InvoiceDetail.Status.DA_THANH_TOAN,
                        Invoice.PaymentMethod.TIEN_MAT
                );
            }
//      cap nhat tong hoa don va so tien da tra
            invoiceService.updateTotal(invoice, amount);
            invoiceService.updatePaidAmount(invoice, amount);
        }
//        truong hop QR chuyen khoan -> tao qr -> neu quet thanh cong -> webhook co id cua cac laborder + phieu kham
//        -> tim hoa don -> tao chi tiet hoa don va cap nhat so tien
    }

    @Override
    public void updateMedicalRecordInvoiceForCash(MedicalRequest.UpdatePaymentRequest request) {
        MedicalRecord record = findById(request.getMedicalRecordId());
        Invoice invoice = record.getInvoice();
//      danh sach chi dinh can thanh toan
        List<Integer> healthPlanIds = record.getLabOrders().stream()
                .map(it -> {
                    if(request.getLabOrderIds().contains(it.getId())){
                        return it.getHealthPlan().getId();
                    }
                    return null;
                })
                .toList();
//      danh sach chi tiet hoa don can sua
        invoice.getInvoiceDetails().forEach(it -> {
            if(healthPlanIds.contains(it.getHealthPlan().getId())){
                it.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
                it.setPaymentMethod(Invoice.PaymentMethod.TIEN_MAT);
            }
        });

//      so tien da thanh toan
        BigDecimal amount = invoice.getInvoiceDetails().stream()
                .filter(d -> healthPlanIds.contains(d.getHealthPlan().getId()))
                .map(d -> d.getFee() == null ? BigDecimal.ZERO : d.getFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoiceService.updatePaidAmount(invoice, amount);
    }

    @Override
    public void update(MedicalRequest request) {
        MedicalRecord medicalRecord = repository.findById(request.getId()).orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOTFOUND)
        );
//
//        Doctor doctor = new Doctor();
//        doctor.setId(request.getDoctorId());
//        medicalRecord.setDoctor(doctor);
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setClinicalExamination(request.getClinicalExamination());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTreatmentPlan(request.getTreatmentPlan());
        medicalRecord.setNote(request.getNote());
        repository.save(medicalRecord);
    }

    @Override
    public void updateStatus(Integer id, MedicalRecord.RecordStatus status) {
        MedicalRecord record = repository.findById(id).orElseThrow(() -> new RuntimeException("Medical record not found"));
        record.setStatus(status);
        repository.save(record);
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
    public List<MedicalResponse> findByPatientId(Integer patientId) {
        return repository.findByPatientId(patientId).stream()
                .map(mapper::toResponse)
                .toList();
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

    @Override
    public void webhookPayosForCheckStatus(WebhookRequest request) {
        WebhookRequest.DataPayload data = request.getData();
        Invoice invoice = invoiceService.findByPayosOrder(data.getOrderCode());

//      truong hop thanh toan lan 2
        if(invoice == null) {
            handlePaymentV2(data);
            return;
        }
        handlePaymentV1(data, invoice);
    }
    private void handlePaymentV2(WebhookRequest.DataPayload data) {
        String desc = data.getDesc();
//          lay id phieu kham tu mo ta T70X11D12D13D14
        String recordIdStr = desc.substring(1, desc.indexOf("X"));
        Integer recordId = Integer.parseInt(recordIdStr);
        MedicalRecord record = findById(recordId);
        Invoice invoice = record.getInvoice();
//      lay danh sach dich vu kham tu mo ta
        String listServiceStr = desc.substring(desc.indexOf("X") + 1);
        String[] serviceIdsStr = listServiceStr.split("D");
        List<Integer> serviceIds = Arrays.stream(serviceIdsStr)
                .map(Integer::parseInt)
                .toList();
        List<InvoiceDetail> invoiceDetails = invoice.getInvoiceDetails().stream()
                .map(detail -> {
                    if(serviceIds.contains(detail.getHealthPlan().getId())){
                        detail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
                        detail.setPaymentMethod(Invoice.PaymentMethod.CHUYEN_KHOAN);
                        return invoiceDetailRepository.save(detail);
                    }
                    return detail;
                })
                .toList();
        invoice.setInvoiceDetails(invoiceDetails);

        invoiceService.updateTotal(invoice, BigDecimal.valueOf(data.getAmount()));
        invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));

//      xoa flag khoi redis
        redisTemplate.delete("PAYMENT_" + invoice.getId());
        log.error(desc);
    }
    private void handlePaymentV1(WebhookRequest.DataPayload data, Invoice invoice) {
        if(invoice.getStatus() == Invoice.PaymentStatus.DA_THANH_TOAN) {
            return;
        }
        if (data.getCode().equals("00")) {
            invoice.setStatus(Invoice.PaymentStatus.DA_THANH_TOAN);
            List<InvoiceDetail> invoiceDetails = invoice.getInvoiceDetails();
            for (InvoiceDetail detail : invoiceDetails) {
                detail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
                invoiceDetailRepository.save(detail);
            }
        }
        invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));
    }

    private MedicalResponse buildResponse(MedicalRecord record) {
        MedicalResponse response = mapper.toResponse(record);
        Patient patient = record.getPatient();
        response.setPatientId(patient.getId());
        response.setPatientName(patient.getFullName());
        response.setPatientGender(patient.getGender());
        response.setPatientAddress(patient.getAddress());
        response.setPatientGender(patient.getGender());
        response.setPatientPhone(patient.getPhone());

//      danh dau cac dich vu con
        HealthPlan healthPlanParent = record.getHealthPlan();
        List<Integer> healthPlanChildren = healthPlanParent.getHealthPlanDetails()
                .stream()
                .map(it -> it.getServiceDetail().getId())
                .toList();

        if(record.getInvoice() != null){
            response.setInvoiceId(record.getInvoice().getId());
        }
        List<LabOrderResponse> services = new ArrayList<>();
        List<LabOrder> labOrders = record.getLabOrders();
        if (labOrders != null) {
            labOrders.forEach(labOrder -> {
                HealthPlan healthPlan = labOrder.getHealthPlan();
                LabOrderResponse detail = labOrderMapper.toResponse(labOrder);
                detail.setHealthPlanId(healthPlan.getId());
                detail.setHealthPlanName(healthPlan.getName());
                if (labOrder.getPerformingDoctor() != null) {
                    detail.setDoctorPerformed(labOrder.getPerformingDoctor().getFullName());
                    detail.setDoctorPerformedId(labOrder.getPerformingDoctor().getId());
                }
                if (labOrder.getOrderingDoctor() != null) {
                    detail.setDoctorOrdered(labOrder.getOrderingDoctor().getFullName());
                }
                List<Integer> healthPlanUnPay = invoiceDetailRepository.findServicesUnpay(record.getId());
                if (healthPlanUnPay.contains(healthPlan.getId())) {
                    detail.setStatusPayment("DA_THANH_TOAN");
                } else {
                    detail.setStatusPayment("CHUA_THANH_TOAN");
                }

                if(healthPlanChildren.contains(healthPlan.getId())){
                    detail.setServiceParent(healthPlanParent.getName());
                }
                services.add(detail);
            });
        }
        response.setLabOrdersResponses(services);
        return response;
    }

//    check da thu tien chua
}
