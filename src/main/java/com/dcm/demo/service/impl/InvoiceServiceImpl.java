package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.response.InvoiceDetailResponse;
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
import org.springframework.web.client.RestTemplate;

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
    private Integer defaultPrice = 2000;

    @Override
    public Invoice findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    @Override
    public InvoiceResponse createInvoiceForQR(InvoiceRequest request) {
        Invoice invoice = mapper.toEntity(request);
        invoice.setCode("HD" + System.currentTimeMillis() / 1000);
        invoice.setPayosOrder(request.getOrderCode());
        repository.save(invoice);

        InvoiceDetail.Status status = InvoiceDetail.Status.DA_THANH_TOAN;
        if (request.getHealthPlanIds() != null && !request.getHealthPlanIds().isEmpty()) {
            HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanIds().get(0));
//      benh nhan chon goi kham (goi kham bao gom phi kham benh)
            if (healthPlan != null && healthPlan.getType().equals(HealthPlan.ServiceType.DICH_VU) && healthPlan.getId() != 1) {
                buildInvoiceDetail(invoice, healthPlan.getId(), healthPlan.getPrice(), status, Invoice.PaymentMethod.CHUYEN_KHOAN, BigDecimal.ZERO);
                updateTotal(invoice, healthPlan.getPrice());
                return buildPayosResponse(invoice.getId(), healthPlan.getPrice().intValue());
            }
//      dich vu kham
            if (healthPlan != null && healthPlan.getId() != 1) {
                buildInvoiceDetail(invoice, healthPlan.getId(), healthPlan.getPrice(), status, Invoice.PaymentMethod.CHUYEN_KHOAN, BigDecimal.ZERO);
//                buildInvoiceDetail(invoice, 1, BigDecimal.valueOf(defaultPrice), status, Invoice.PaymentMethod.CHUYEN_KHOAN, BigDecimal.ZERO);
                updateTotal(invoice, healthPlan.getPrice());
                return buildPayosResponse(invoice.getId(), healthPlan.getPrice().intValue());
            }
        }

//      kham bac si
        Doctor doctor = doctorService.findById(request.getDoctorId());
        buildInvoiceDetail(invoice, 1, doctor.getDegree().getExaminationFee(), status, Invoice.PaymentMethod.CHUYEN_KHOAN, BigDecimal.ZERO);
        updateTotal(invoice, doctor.getDegree().getExaminationFee());

        return buildPayosResponse(invoice.getId(),  doctor.getDegree().getExaminationFee().intValue());
    }

    private InvoiceResponse buildPayosResponse(Integer id, Integer examFee) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(id);
        response.setExamFee(examFee);
        return response;
    }

    @Override
    public void createInvoiceForCash(MedicalRecord record) {
//      tao hoa don
        Invoice invoice = buildInvoice(record);
        repository.save(invoice);

        InvoiceDetail.Status status = InvoiceDetail.Status.DA_THANH_TOAN;
        HealthPlan healthPlan = record.getHealthPlan();

        if (healthPlan != null && healthPlan.getId() != 1) {
            buildInvoiceDetail(invoice, healthPlan.getId(), healthPlan.getPrice(), status, Invoice.PaymentMethod.TIEN_MAT, healthPlan.getPrice());

//          cap nhat tong tien va da thanh toan
            updateTotal(invoice, healthPlan.getPrice());
            updatePaidAmount(invoice, healthPlan.getPrice());
            return;
        }

//      kham rieng bac si
        Doctor doctor = record.getDoctor();
        buildInvoiceDetail(invoice, 1, doctor.getDegree().getExaminationFee(), InvoiceDetail.Status.DA_THANH_TOAN, Invoice.PaymentMethod.TIEN_MAT, doctor.getDegree().getExaminationFee());
        updateTotal(invoice, doctor.getDegree().getExaminationFee());
        updatePaidAmount(invoice, doctor.getDegree().getExaminationFee());
    }

    static Invoice buildInvoice(MedicalRecord record) {
        Invoice invoice = new Invoice();
        invoice.setMedicalRecord(record);
        invoice.setCode("HD" + System.currentTimeMillis() / 1000);
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
    public void buildInvoiceDetail(Invoice invoice, Integer healthPlanId, BigDecimal feeService, InvoiceDetail.Status status, Invoice.PaymentMethod paymentMethod, BigDecimal paymentAmount) {
        InvoiceDetail detail = new InvoiceDetail();
        detail.setInvoice(invoice);
        detail.setPaymentMethod(paymentMethod);
        detail.setStatus(status);
        detail.setPaidAmount(paymentAmount);
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
