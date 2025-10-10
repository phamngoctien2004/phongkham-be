package com.dcm.demo.service.impl;

import com.dcm.demo.Utils.ConvertUtil;
import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.request.PaymentRequest;
import com.dcm.demo.dto.request.WebhookRequest;
import com.dcm.demo.dto.response.InvoiceDetailResponse;
import com.dcm.demo.dto.response.MedicalResponse;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.LabOrderMapper;
import com.dcm.demo.mapper.MedicalMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.InvoiceDetailRepository;
import com.dcm.demo.repository.LabOrderRepository;
import com.dcm.demo.repository.MedicalRecordRepository;
import com.dcm.demo.repository.RelationshipRepository;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private final DepartmentService departmentService;
    private final InvoiceService invoiceService;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final LabOrderRepository labOrderRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TemplateEngine templateEngine;
    private BigDecimal defaultPrice = BigDecimal.valueOf(2000);

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
    public String exportHtml(Integer id) {
        MedicalRecord medicalRecord = repository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOTFOUND)
        );
        Patient patient = medicalRecord.getPatient();
        Doctor doctor = medicalRecord.getDoctor();

        Map<String, Object> params = new LinkedHashMap<>();

        params.put("patientName", patient.getFullName());
        params.put("patientGender", patient.getGender() != null ? patient.getGender().toString() : "");
        params.put("patientAddress", patient.getAddress());
        params.put("patientPhone", patient.getPhone());
        params.put("patientBirthDate", patient.getBirth());
        params.put("insuranceNumber", "1023888519");

        params.put("appointmentDate", medicalRecord.getDate().toLocalDate());
        params.put("appointmentTime", medicalRecord.getDate().toLocalTime());

        params.put("doctorName", doctor.getFullName());
        params.put("roomName", departmentService.getRoomFromDepartment(doctor.getDepartment()));
        params.put("departmentName", doctor.getDepartment().getName());

        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(locale);
        String formattedFee = currencyVN.format(medicalRecord.getFee()); // ví dụ examFee = 150000 → "150.000 ₫"

        params.put("examFee", formattedFee);
        Context ctx = new Context();
        ctx.setVariables(params);

        return templateEngine.process("pdfs/phieukham.html", ctx);
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

//      thu phi kham benh kham bac si rieng
        if (request.getDoctorId() != null) {
            Doctor doctor = doctorService.findById(request.getDoctorId());
            medicalRecord.setDoctor(doctor);
            HealthPlan healthPlan = new HealthPlan();
            healthPlan.setId(1);
            medicalRecord.setFee(doctor.getDegree().getExaminationFee());
            medicalRecord.setHealthPlan(healthPlan);
        }

//      khong tao health plan phi kham benh
        if (request.getHealthPlanId() != null) {
            HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
            medicalRecord.setHealthPlan(healthPlan);
//          gan bac si kham ban dau cho phieu kham (bac si nay se la nguoi thuc hien kham, ke don, ...)
            Doctor doctor = new Doctor();

//          phi kham ung truoc
            medicalRecord.setFee(defaultPrice);
            doctor.setId(1);
            medicalRecord.setDoctor(doctor);
        }
        MedicalRecord saved = repository.save(medicalRecord);
//      thanh toan online
        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceService.findById(request.getInvoiceId());
            invoice.setMedicalRecord(saved);
            invoiceService.save(invoice);
        }
//      thanh toan tien mat
        else {
            invoiceService.createInvoiceForCash(saved);
        }
        return saved;
    }


//    //    thanh toan tien mat tu lan 2 tro di
//    @Override
//    public void updatePaymentForLabOrder(MedicalRequest.UpdatePaymentRequest request) {
//        MedicalRecord medicalRecord = findById(request.getMedicalRecordId());
//        List<Integer> labOrderIds = request.getLabOrderIds();
////      filter laborder duoc chon tu phieu kham
//        List<LabOrder> labOrders = medicalRecord.getLabOrders().stream()
//                .filter(it -> labOrderIds.contains(it.getId()))
//                .toList();
////      tao chi tiet hoa don
//        Invoice invoice = medicalRecord.getInvoice();
//        if (invoice != null) {
//            BigDecimal amount = BigDecimal.ZERO;
//            for (LabOrder labOrder : labOrders) {
//                invoiceService.buildInvoiceDetail(
//                        invoice,
//                        labOrder.getHealthPlan().getId(),
//                        labOrder.getPrice(),
//                        InvoiceDetail.Status.DA_THANH_TOAN,
//                        Invoice.PaymentMethod.TIEN_MAT
//                );
//            }
//            invoiceService.updateTotal(invoice, amount);
//            invoiceService.updatePaidAmount(invoice, amount);
//        }
//
//    }

    @Override
    public void updateMedicalRecordInvoiceForCash(PaymentRequest request) {
        MedicalRecord record = findById(request.getMedicalRecordId());
        Invoice invoice = record.getInvoice();
        List<InvoiceDetail> invoiceDetails = invoiceDetailRepository.findByInvoiceIdAndHealthPlanIdIn(invoice.getId(), request.getHealthPlanIds());
        for(InvoiceDetail detail : invoiceDetails) {
            detail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
            detail.setPaymentMethod(Invoice.PaymentMethod.TIEN_MAT);
            detail.setPaidAmount(detail.getFee());
            invoiceDetailRepository.save(detail);
        }

        invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(request.getTotalAmount()));
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
        if (invoice == null) {
            handlePaymentV2(data);
            return;
        }
        handlePaymentV1(request, invoice);
    }

    private void handlePaymentV2(WebhookRequest.DataPayload data) {

        String desc = data.getDescription();
//      lay id phieu kham tu mo ta T70X11D12D13D14
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

//      cap nhat trang thai chi tiet hoa donq
        List<InvoiceDetail> invoiceDetails = invoice.getInvoiceDetails().stream()
                .map(detail -> {
                    if (serviceIds.contains(detail.getHealthPlan().getId())) {
                        detail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
                        detail.setPaymentMethod(Invoice.PaymentMethod.CHUYEN_KHOAN);
                        detail.setPaidAmount(detail.getFee());
                        return invoiceDetailRepository.save(detail);
                    }
                    return detail;
                })
                .toList();

        invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));
    }

    private void handlePaymentV1(WebhookRequest request, Invoice invoice) {
        if (!request.isSuccess()) {
            log.error("not payment");
        }
        WebhookRequest.DataPayload data = request.getData();
        if (data.getCode().equals("00")) {
            List<InvoiceDetail> invoiceDetails = invoice.getInvoiceDetails();
            HealthPlan healthPlan = invoiceDetails.get(0).getHealthPlan();

//          goi kham
            if (healthPlan.getType().equals(HealthPlan.ServiceType.DICH_VU)) {
                InvoiceDetail invoiceDetail = invoice.getInvoiceDetails().get(0);
                invoiceDetail.setPaidAmount(BigDecimal.valueOf(data.getAmount()));
                invoiceDetail.setStatus(InvoiceDetail.Status.THANH_TOAN_MOT_PHAN);
                invoiceDetailRepository.save(invoiceDetail);
                invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));
                log.error("goi kham");
                return;
            }
//          dich vu le
            if (healthPlan.getId() != 1) {
                InvoiceDetail invoiceDetail = invoice.getInvoiceDetails().stream()
                        .filter(it -> it.getHealthPlan().getId().equals(1))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Invoice detail not found"));
                invoiceDetail.setPaidAmount(BigDecimal.valueOf(data.getAmount()));
                invoiceDetail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
                invoiceDetailRepository.save(invoiceDetail);
                invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));
                log.error("goi le");

                return;
            }
            // kham chuyen khoa
            InvoiceDetail invoiceDetail = invoice.getInvoiceDetails().get(0);
            invoiceDetail.setPaidAmount(BigDecimal.valueOf(data.getAmount()));
            invoiceDetail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
            invoiceDetailRepository.save(invoiceDetail);
            invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));
            log.error("goi chuyen khoa");

        }

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

//      lay danh sach chi tiet hoa don

        Invoice invoice = record.getInvoice();
        if(invoice == null) {
            return response;
        }
        List<InvoiceDetail> invoiceDetails = invoice.getInvoiceDetails();

        List<InvoiceDetailResponse> details = new ArrayList<>();
//      tao ra lab detail
        for (InvoiceDetail detail : invoiceDetails) {
            InvoiceDetailResponse invoiceDetailResponse = ConvertUtil.convert(detail);
            HealthPlan healthPlan = detail.getHealthPlan();

//          gan thong tin dich vu goc
            response.setHealthPlanId(healthPlan.getId());
            response.setHealthPlanName(healthPlan.getName());
//          dich vu le
            if (!healthPlan.getType().equals(HealthPlan.ServiceType.DICH_VU)) {
                LabOrder labOrder = labOrderRepository.findByMedicalRecordIdAndHealthPlanId(record.getId(), healthPlan.getId())
                        .orElseThrow(() -> new RuntimeException("LabOrder not found"));
                InvoiceDetailResponse.LabDetail labDetail = ConvertUtil.convert(labOrder);
                labDetail.setName(healthPlan.getName());

                invoiceDetailResponse.setSingleLab(labDetail);
                invoiceDetailResponse.setTypeService("SINGLE");
                details.add(invoiceDetailResponse);
                continue;
            }

//          goi kham
            List<Integer> healthPlanIds = healthPlan.getHealthPlanDetails().stream()
                    .map(it -> it.getServiceDetail().getId())
                    .toList();
//          tim danh sach chi dinh cua phieu kham theo danh sach healthplanid
            List<InvoiceDetailResponse.LabDetail> labDetails = labOrderRepository.findByMedicalRecordIdAndHealthPlanIdIn(record.getId(), healthPlanIds).stream()
                    .map(it -> {
                        InvoiceDetailResponse.LabDetail labDetail = ConvertUtil.convert(it);
                        labDetail.setName(it.getHealthPlan().getName());
                        return labDetail;
                    })
                    .toList();
            invoiceDetailResponse.setMultipleLab(labDetails);
            invoiceDetailResponse.setTypeService("MULTIPLE");
            details.add(invoiceDetailResponse);
        }
//      neu dich vu la goi kham thi tao multi lab

        response.setInvoiceDetailsResponse(details);
        response.setTotal(invoice.getTotalAmount().intValue());
        response.setPaid(invoice.getPaidAmount().intValue());
        return response;
    }
}
