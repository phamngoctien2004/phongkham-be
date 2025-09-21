package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.response.InvoiceResponse;
import com.dcm.demo.mapper.InvoiceMapper;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.model.InvoiceDetail;
import com.dcm.demo.repository.InvoiceDetailRepository;
import com.dcm.demo.repository.InvoiceRepository;
import com.dcm.demo.service.interfaces.DoctorService;
import com.dcm.demo.service.interfaces.HealthPlanService;
import com.dcm.demo.service.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository repository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final DoctorService doctorService;
    private final HealthPlanService healthPlanService;
    private final InvoiceMapper mapper;

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

        Invoice savedInvoice = repository.save(invoice);
        if (request.getHealthPlanIds() != null && !request.getHealthPlanIds().isEmpty()) {
            for (Integer healthPlanId : request.getHealthPlanIds()) {
                HealthPlan healthPlan = healthPlanService.findById(healthPlanId);
                buildInvoiceDetail(invoice, healthPlanId, healthPlan.getPrice());
            }
        }
        if (!examinationFee.equals(BigDecimal.ZERO)) {
            buildInvoiceDetail(invoice, null, examinationFee);
        }
        return mapper.toResponse(savedInvoice);
    }

    @Override
    public void updateStatusPayment(Long payOsCode, String codeStatus, Long amount) {
        Invoice invoice = repository.findByPayosOrder(payOsCode).orElseThrow(() -> new RuntimeException("Invoice not found with code: " + payOsCode));
        if(invoice.getStatus() == Invoice.PaymentStatus.HOAN_THANH) {
            return;
        }
        if (codeStatus.equals("00")) {
            invoice.setStatus(Invoice.PaymentStatus.HOAN_THANH);
        }
        invoice.setPaidAmount(invoice.getPaidAmount().add(BigDecimal.valueOf(amount)));
        repository.save(invoice);
    }

    @Override
    public boolean checkStatusPayment(Integer invoiceId) {
        return repository.findById(invoiceId)
                .map(invoice -> invoice.getStatus() == Invoice.PaymentStatus.HOAN_THANH)
                .orElse(false);
    }

    private void buildInvoiceDetail(Invoice invoice, Integer healthPlanId, BigDecimal feeService) {

        InvoiceDetail detail = new InvoiceDetail();
        detail.setInvoice(invoice);
        detail.setPaymentMethod(Invoice.PaymentMethod.CHUYEN_KHOAN);

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
