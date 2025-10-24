package com.dcm.demo.service.impl;

import com.dcm.demo.Utils.ConvertUtil;
import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.request.PaymentRequest;
import com.dcm.demo.dto.request.WebhookRequest;
import com.dcm.demo.dto.response.*;
import com.dcm.demo.enums.Event;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.MedicalMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.*;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final SimpMessagingTemplate messaging;
    private final InvoiceService invoiceService;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final LabOrderRepository labOrderRepository;
    private final AppointmentRepository appointmentRepository;
    private BigDecimal defaultPrice = BigDecimal.valueOf(2000);


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

        if (request.getHealthPlanId() != null && request.getHealthPlanId() > 0) {
            HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
            medicalRecord.setHealthPlan(healthPlan);
        }
        MedicalRecord saved = repository.save(medicalRecord);

//      da dat lich va thanh toan
        if (request.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));
            String invoiceCode = appointment.getInvoiceCode();
            Invoice invoice = invoiceService.findByCode(invoiceCode);
            invoice.setMedicalRecord(saved);
            invoiceService.save(invoice);
            log.error("Lien ket phieu kham voi hoa don dat lich thanh cong");
        } else {
//          thanh toan online
            if (request.getInvoiceId() != null) {
                Invoice invoice = invoiceService.findById(request.getInvoiceId());
                invoice.setMedicalRecord(saved);
                invoiceService.save(invoice);
            }
//          thanh toan tien mat
            else {
                invoiceService.createInvoiceForCash(saved);
            }
        }


        return saved;
    }


    @Override
    public void updateMedicalRecordInvoiceForCash(PaymentRequest request) {
        MedicalRecord record = findById(request.getMedicalRecordId());
        Invoice invoice = record.getInvoice();
        List<InvoiceDetail> invoiceDetails = invoiceDetailRepository.findByInvoiceIdAndHealthPlanIdIn(invoice.getId(), request.getHealthPlanIds());
        for (InvoiceDetail detail : invoiceDetails) {
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
        if (record.getStatus().equals(MedicalRecord.RecordStatus.CHO_KHAM)) {
            User user = userService.getCurrentUser();
            Doctor doctor = user.getDoctor();
            record.setDoctor(doctor);
        }
        log.error("Update medical record id {} status from {} to {}", id, record.getStatus(), status);
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
                .map(this::buildResponse)
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
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public Page<MedicalResponse> findAll(String keyword, MedicalRecord.RecordStatus status, LocalDate date, Pageable pageable) {
        LocalDateTime from = null, to = null;
        if (date != null) {
            from = date.atStartOfDay();
            to = date.plusDays(1).atStartOfDay();
        }
        return repository.findAll(keyword, from, to, status, null, null, pageable)
                .map(it -> buildResponse(it.getPatient(), it));
    }

    @Override
    public Page<MedicalResponse> findAllByDoctor(String keyword, MedicalRecord.RecordStatus status, LocalDate date, boolean isAllDepartment, Pageable pageable) {
        LocalDateTime from = null, to = null;
        if (date != null) {
            from = date.atStartOfDay();
            to = date.plusDays(1).atStartOfDay();
        }

        User user = userService.getCurrentUser();
        Doctor doctor = user.getDoctor();
        Integer doctorId = null;
        Integer departmentId = null;
        if (!isAllDepartment) {
            doctorId = doctor.getId();
        } else {
            departmentId = doctor.getDepartment().getId();
        }
        return repository.findAll(keyword, from, to, status, doctorId, departmentId, pageable)
                .map(it -> buildResponse(it.getPatient(), it));
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
        if (data.getDescription().contains("DLK")) {
            Integer id = Integer.parseInt(data.getDescription().substring(3));
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));
            appointment.setStatus(Appointment.AppointmentStatus.DA_XAC_NHAN);
            appointmentRepository.save(appointment);
            log.error("Payment for DLK service received.");

//         push event
            messaging.convertAndSend(
                    "/topic/appointments." + appointment.getId(),
                    new PaymentEvent(Event.PAYMENT_SUCCESS, "Payment successful for appointment", appointment.getId())
            );
            return;
        }
        log.error("Payment for QR 1");
        messaging.convertAndSend(
                "/topic/invoice." + invoice.getId(),
                new PaymentEvent(Event.PAYMENT_SUCCESS, "Payment successful", invoice.getId())
        );

    }

    @Override
    public InvoiceResponse getInvoiceByMedicalRecordId(Integer medicalRecordId) {
        MedicalRecord record = findById(medicalRecordId);
        Invoice invoice = record.getInvoice();
        if (invoice == null) {
            throw new RuntimeException("Invoice not found");
        }
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setCode(invoice.getCode());
        invoiceResponse.setTotalAmount(invoice.getTotalAmount().intValue());
        invoiceResponse.setPaidAmount(invoice.getPaidAmount().intValue());
        invoiceResponse.setStatus(invoice.getStatus());
        invoiceResponse.setId(invoice.getId());
        invoiceResponse.setPaymentMethod(invoice.getPaymentMethod().toString());
        invoiceResponse.setDate(invoice.getPaymentDate());
        return invoiceResponse;
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

//      cap nhat trang thai chi tiet hoa don
        invoice.getInvoiceDetails().forEach(detail -> {
            if (serviceIds.contains(detail.getHealthPlan().getId())) {
                detail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
                detail.setPaymentMethod(Invoice.PaymentMethod.CHUYEN_KHOAN);
                detail.setPaidAmount(detail.getFee());
            }
        });

        invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));
        messaging.convertAndSend(
                "/topic/invoice." + invoice.getId(),
                new PaymentEvent(Event.PAYMENT_SUCCESS, "Payment successful", invoice.getId())
        );
        log.error("Payment for QR 22222.");
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
                invoiceDetail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
                invoiceDetailRepository.save(invoiceDetail);
                invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));
                log.error("goi kham");
                return;
            }
//          dich vu lkhams
            if (healthPlan.getId() != 1) {
                InvoiceDetail invoiceDetail = invoice.getInvoiceDetails().stream()
                        .filter(it -> !it.getHealthPlan().getId().equals(1))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Invoice detail not found"));
                invoiceDetail.setPaidAmount(BigDecimal.valueOf(data.getAmount()));
                invoiceDetail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
                invoiceDetailRepository.save(invoiceDetail);
                invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));
                log.error("goi xn / chuyen khoa");

                return;
            }
            // kham bac si
            InvoiceDetail invoiceDetail = invoice.getInvoiceDetails().get(0);
            invoiceDetail.setPaidAmount(BigDecimal.valueOf(data.getAmount()));
            invoiceDetail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
            invoiceDetailRepository.save(invoiceDetail);
            invoiceService.updatePaidAmount(invoice, BigDecimal.valueOf(data.getAmount()));
            log.error("kham bac si");

        }

    }

    private MedicalResponse buildResponse(Patient patient, MedicalRecord record) {
        MedicalResponse response = mapper.toResponse(record);
        response.setPatientId(patient.getId());
        response.setPatientName(patient.getFullName());
        response.setPatientGender(patient.getGender());
        response.setPatientAddress(patient.getAddress());
        response.setPatientGender(patient.getGender());
        response.setPatientPhone(patient.getPhone());
        if (record.getDoctor() != null) {
            response.setDoctorName(record.getDoctor().getFullName());
        }
        return response;
    }

    private MedicalResponse buildResponse(MedicalRecord record) {
        Patient patient = record.getPatient();

        MedicalResponse response = buildResponse(patient, record);

//      lay danh sach chi tiet hoa don

        Invoice invoice = record.getInvoice();
        if (invoice == null) {
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
