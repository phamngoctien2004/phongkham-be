package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.response.InvoiceResponse;
import com.dcm.demo.mapper.InvoiceMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.InvoiceDetailRepository;
import com.dcm.demo.repository.InvoiceRepository;
import com.dcm.demo.service.interfaces.DoctorService;
import com.dcm.demo.service.interfaces.HealthPlanService;
import com.dcm.demo.service.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository repository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final DoctorService doctorService;
    private final HealthPlanService healthPlanService;
    private final InvoiceMapper mapper;

    @Override
    public Invoice findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        Invoice invoice = mapper.toEntity(request);
        invoice.setCode("HD" + System.currentTimeMillis() / 1000);
        invoice.setPayosOrder(request.getOrderCode());
        if (request.getHealthPlanIds() != null && !request.getHealthPlanIds().isEmpty()) {
            invoice.setTotalAmount(healthPlanService.calcTotalService(request.getHealthPlanIds()));
        }
        BigDecimal examinationFee = BigDecimal.ZERO;
        if (request.getDoctorId() != null) {
            Doctor doctor = doctorService.findById(request.getDoctorId());
            examinationFee = examinationFee.add(doctor.getDegree().getExaminationFee());
            invoice.setTotalAmount(examinationFee);
        }
        invoice.setPaymentMethod(Invoice.PaymentMethod.CHUYEN_KHOAN);
        Invoice savedInvoice = repository.save(invoice);
        if (request.getHealthPlanIds() != null && !request.getHealthPlanIds().isEmpty()) {
            for (Integer healthPlanId : request.getHealthPlanIds()) {
                HealthPlan healthPlan = healthPlanService.findById(healthPlanId);
                buildInvoiceDetail(invoice, healthPlanId, healthPlan.getPrice(), InvoiceDetail.Status.CHUA_THANH_TOAN);
            }
        }
        if (!examinationFee.equals(BigDecimal.ZERO)) {
            buildInvoiceDetail(invoice, null, examinationFee, InvoiceDetail.Status.CHUA_THANH_TOAN);
        }
        return mapper.toResponse(savedInvoice);
    }

    @Override
    public void createDetailInvoice(MedicalRecord record) {
        Invoice invoice = buildInvoice(record);
        repository.save(invoice);

        HealthPlan healthPlan = record.getHealthPlan();
        if (healthPlan != null && healthPlan.getHealthPlanDetails() != null) {
            for (healthPlanDetail detail : healthPlan.getHealthPlanDetails()) {
                HealthPlan serviceDetail = detail.getServiceDetail();
                buildInvoiceDetail(invoice, serviceDetail.getId(), serviceDetail.getPrice(), InvoiceDetail.Status.DA_THANH_TOAN);
            }
            buildInvoiceDetail(invoice, 1,BigDecimal.ZERO, InvoiceDetail.Status.DA_THANH_TOAN);
            return;
        }

        if (healthPlan != null && healthPlan.getId() != 1) {
            buildInvoiceDetail(invoice, 1,BigDecimal.ZERO, InvoiceDetail.Status.DA_THANH_TOAN);
            buildInvoiceDetail(invoice, healthPlan.getId(), healthPlan.getPrice(), InvoiceDetail.Status.DA_THANH_TOAN);
            return;
        }

        Doctor doctor = record.getDoctor();
        buildInvoiceDetail(invoice, 1, doctor.getDegree().getExaminationFee(), InvoiceDetail.Status.DA_THANH_TOAN);
    }

    static Invoice buildInvoice(MedicalRecord record) {
        Invoice invoice = new Invoice();
        invoice.setMedicalRecord(record);
        invoice.setCode("HD" + System.currentTimeMillis() / 1000);
        invoice.setTotalAmount(record.getTotal());
        invoice.setPaidAmount(record.getTotal());
        invoice.setStatus(Invoice.PaymentStatus.DA_THANH_TOAN);
        invoice.setPaymentMethod(Invoice.PaymentMethod.TIEN_MAT);
        return invoice;
    }

    @Override
    public void updateStatusPayment(Long payOsCode, String codeStatus, Long amount) {
        Invoice invoice = repository.findByPayosOrder(payOsCode).orElseThrow(() -> new RuntimeException("Invoice not found with code: " + payOsCode));
        if (invoice.getStatus() == Invoice.PaymentStatus.DA_THANH_TOAN) {
            return;
        }
        if (codeStatus.equals("00")) {
            invoice.setStatus(Invoice.PaymentStatus.DA_THANH_TOAN);
            List<InvoiceDetail> invoiceDetails = invoice.getInvoiceDetails();
            for (InvoiceDetail detail : invoiceDetails) {
                detail.setStatus(InvoiceDetail.Status.DA_THANH_TOAN);
                invoiceDetailRepository.save(detail);
            }
        }
        invoice.setPaidAmount(invoice.getPaidAmount().add(BigDecimal.valueOf(amount)));
        repository.save(invoice);
    }

    @Override
    public void updateTotalAmount(Integer recordId, BigDecimal amount) {
    }

    @Override
    public boolean checkStatusPayment(Integer invoiceId) {
        return repository.findById(invoiceId)
                .map(invoice -> invoice.getStatus() == Invoice.PaymentStatus.DA_THANH_TOAN)
                .orElse(false);
    }

    @Override
    public List<Integer> getServicesUnPay(Integer recordId) {
        return invoiceDetailRepository.findServicesUnpay(recordId);
    }

    @Override
    public void save(Invoice invoice) {
        repository.save(invoice);
    }

    private void buildInvoiceDetail(Invoice invoice, Integer healthPlanId, BigDecimal feeService, InvoiceDetail.Status status) {
        InvoiceDetail detail = new InvoiceDetail();
        detail.setInvoice(invoice);
        detail.setPaymentMethod(invoice.getPaymentMethod());
        detail.setStatus(status);
        if (healthPlanId != null) {
            HealthPlan healthPlan = healthPlanService.findById(healthPlanId);
            healthPlan.setId(healthPlanId);
            detail.setHealthPlan(healthPlan);
            detail.setDescription(healthPlan.getName());
            detail.setFee(healthPlan.getPrice());
        } else {
            detail.setFee(feeService);
            detail.setDescription("Phí khám bệnh");
        }
        invoiceDetailRepository.save(detail);
    }
}
