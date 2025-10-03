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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository repository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final DoctorService doctorService;
    private final HealthPlanService healthPlanService;
    private final InvoiceMapper mapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Invoice findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    @Override
    public InvoiceResponse createInvoiceForQR(InvoiceRequest request) {
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
                if(healthPlan.getHealthPlanDetails() != null && !healthPlan.getHealthPlanDetails().isEmpty()) {
                    buildInvoiceDetail(invoice, 1, BigDecimal.ZERO, InvoiceDetail.Status.DA_THANH_TOAN, Invoice.PaymentMethod.CHUYEN_KHOAN);
                    List<HealthPlan> serviceDetails = healthPlan.getHealthPlanDetails().stream().map(healthPlanDetail::getServiceDetail).toList();
                    for (HealthPlan serviceDetail : serviceDetails) {
                        buildInvoiceDetail(invoice, serviceDetail.getId(), serviceDetail.getPrice(), InvoiceDetail.Status.CHUA_THANH_TOAN, Invoice.PaymentMethod.CHUYEN_KHOAN);
                    }
                    continue;
                }
                buildInvoiceDetail(invoice, healthPlanId, healthPlan.getPrice(), InvoiceDetail.Status.CHUA_THANH_TOAN, Invoice.PaymentMethod.CHUYEN_KHOAN);
            }
        }
//        truong hop kham bac si rieng
        if (!examinationFee.equals(BigDecimal.ZERO)) {
            buildInvoiceDetail(invoice, 1, examinationFee, InvoiceDetail.Status.CHUA_THANH_TOAN, Invoice.PaymentMethod.CHUYEN_KHOAN);
        }
        return mapper.toResponse(savedInvoice);
    }

    @Override
    public void createInvoiceForCash(MedicalRecord record) {
        Invoice invoice = buildInvoice(record);
        repository.save(invoice);

        HealthPlan healthPlan = record.getHealthPlan();
//        truong hop goi kham benh
        if (healthPlan != null && healthPlan.getHealthPlanDetails() != null && !healthPlan.getHealthPlanDetails().isEmpty()) {
            for (healthPlanDetail detail : healthPlan.getHealthPlanDetails()) {
                HealthPlan serviceDetail = detail.getServiceDetail();
                buildInvoiceDetail(invoice, serviceDetail.getId(), serviceDetail.getPrice(), InvoiceDetail.Status.DA_THANH_TOAN, Invoice.PaymentMethod.TIEN_MAT);
            }
            buildInvoiceDetail(invoice, 1, BigDecimal.ZERO, InvoiceDetail.Status.DA_THANH_TOAN, Invoice.PaymentMethod.TIEN_MAT);
            return;
        }
// truong hop chi dinh le
        if (healthPlan != null) {
            buildInvoiceDetail(invoice, 1, BigDecimal.ZERO, InvoiceDetail.Status.DA_THANH_TOAN, Invoice.PaymentMethod.TIEN_MAT);
            buildInvoiceDetail(invoice, healthPlan.getId(), healthPlan.getPrice(), InvoiceDetail.Status.DA_THANH_TOAN, Invoice.PaymentMethod.TIEN_MAT);
            return;
        }

        Doctor doctor = record.getDoctor();
        buildInvoiceDetail(invoice, 1, doctor.getDegree().getExaminationFee(), InvoiceDetail.Status.DA_THANH_TOAN, Invoice.PaymentMethod.TIEN_MAT);
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
    public void updateTotal(Invoice invoice, BigDecimal amount) {
        invoice.setTotalAmount(invoice.getTotalAmount().add(amount));
        invoice.setStatus(invoice.getTotalAmount().equals(invoice.getPaidAmount()) ? Invoice.PaymentStatus.DA_THANH_TOAN : Invoice.PaymentStatus.THANH_TOAN_MOT_PHAN);
        repository.save(invoice);
    }

    @Override
    public void updatePaidAmount(Invoice invoice, BigDecimal amount) {
        invoice.setPaidAmount(invoice.getPaidAmount().add(amount));
        invoice.setStatus(invoice.getTotalAmount().equals(invoice.getPaidAmount()) ? Invoice.PaymentStatus.DA_THANH_TOAN : Invoice.PaymentStatus.THANH_TOAN_MOT_PHAN);
        repository.save(invoice);
    }

    @Override
    public boolean checkStatusPayment(Integer invoiceId) {
        Invoice invoice = repository.findById(invoiceId).orElseThrow(() -> new RuntimeException("Invoice not found"));
        if (invoice.getStatus() == Invoice.PaymentStatus.DA_THANH_TOAN) {
            return true;
        }
        if (invoice.getStatus() == Invoice.PaymentStatus.THANH_TOAN_MOT_PHAN
        && redisTemplate.opsForValue().get("PAYMENT_" + invoiceId) == null) {
            return true;
        }
        return false;
    }

    @Override
    public List<Integer> getServicesUnPay(Integer recordId) {
        return invoiceDetailRepository.findServicesUnpay(recordId);
    }

    @Override
    public void save(Invoice invoice) {
        repository.save(invoice);
    }

    @Override
    public void buildInvoiceDetail(Invoice invoice, Integer healthPlanId, BigDecimal feeService, InvoiceDetail.Status status, Invoice.PaymentMethod paymentMethod) {
        InvoiceDetail detail = new InvoiceDetail();
        detail.setInvoice(invoice);
        detail.setPaymentMethod(paymentMethod);
        detail.setStatus(status);
        if (healthPlanId != null && healthPlanId != 1) {
            HealthPlan healthPlan = healthPlanService.findById(healthPlanId);
            healthPlan.setId(healthPlanId);
            detail.setHealthPlan(healthPlan);
            detail.setDescription(healthPlan.getName());
            detail.setFee(healthPlan.getPrice());
        } else {
            HealthPlan healthPlan = healthPlanService.findById(1);
            detail.setHealthPlan(healthPlan);
            detail.setFee(feeService);
            detail.setDescription("Phí khám bệnh");
        }
        invoiceDetailRepository.save(detail);
    }

    @Override
    public Invoice findByPayosOrder(Long orderCode) {
        return repository.findByPayosOrder(orderCode).orElse(null);
    }
}
