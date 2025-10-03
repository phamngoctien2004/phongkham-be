package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.request.PaymentRequest;
import com.dcm.demo.dto.response.InvoiceResponse;
import com.dcm.demo.dto.response.PaymentResponse;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.model.MedicalRecord;
import com.dcm.demo.service.interfaces.InvoiceService;
import com.dcm.demo.service.interfaces.LabOrderService;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.PaymentData;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayosService {
    private final String apiKey = "06eac658-bada-4cee-92a3-48626b520aab";
    private final String clientId = "355718b7-5f38-44ea-b31e-3e269705aa5b";
    private final String checksumKey = "91e022cb0777e5700cfe57d541e778bf9c744170c957423ac53cbf8e4d40448a";

    private final InvoiceService invoiceService;
    private final LabOrderService labOrderService;
    private final MedicalRecordService medicalRecordService;
    private final RedisTemplate<String, Object> redisTemplate;
    final PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

    public PaymentResponse createLinkPayment(PaymentRequest paymentRequest) {

        try {
            if (paymentRequest.getMedicalRecordId() == null) {
                return createLinkPaymentV1(paymentRequest);
            }
            return createLinkPaymentV2(paymentRequest);

        } catch (Exception ignored) {
            log.error(ignored.getMessage(), ignored);
        }
        return null;
    }

    private PaymentResponse createLinkPaymentV1(PaymentRequest paymentRequest) throws Exception {
        Long orderCode = System.currentTimeMillis() / 1000;

        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setPaymentMethod(Invoice.PaymentMethod.CHUYEN_KHOAN);
        invoiceRequest.setOrderCode(orderCode);
        if (paymentRequest.getDoctorId() != null) {
            invoiceRequest.setDoctorId(paymentRequest.getDoctorId());
        }
        if (paymentRequest.getHealthPlanIds() != null && !paymentRequest.getHealthPlanIds().isEmpty()) {
            invoiceRequest.setHealthPlanIds(paymentRequest.getHealthPlanIds());
        }
        InvoiceResponse invoiceResponse = invoiceService.createInvoiceForQR(invoiceRequest);

        PaymentData paymentData = PaymentData
                .builder()
                .orderCode(orderCode)
                .amount(invoiceResponse.getTotalAmount())
                .description("Thanh toán hoa đơn ")
                .returnUrl("http://localhost:8080")
                .cancelUrl("http://localhost:8080")
                .build();
        return new PaymentResponse(invoiceResponse.getId(), payOS.createPaymentLink(paymentData).getQrCode());
    }

    private PaymentResponse createLinkPaymentV2(PaymentRequest paymentRequest) throws Exception {
        Long orderCode = System.currentTimeMillis() / 1000;
        MedicalRecord medicalRecord = medicalRecordService.findById(paymentRequest.getMedicalRecordId());
        List<LabOrder> labOrders = labOrderService.findByIds(paymentRequest.getLabOrderIds());
        List<Integer> servicesSelected = labOrders
                .stream()
                .map(labOrder -> labOrder.getHealthPlan().getId())
                .toList();
        BigDecimal totalAmount = labOrders
                .stream()
                .map(LabOrder::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String description = "T" + medicalRecord.getId() + "X" + servicesSelected
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining("D"));
        Integer invoiceId = medicalRecord.getInvoice().getId();
        System.out.println(description);
        redisTemplate.opsForValue().set("PAYMENT_" + invoiceId, servicesSelected);
        PaymentData paymentData = PaymentData
                .builder()
                .orderCode(orderCode)
                .amount(totalAmount.intValue())
                .description(description)
                .returnUrl("http://localhost:8080")
                .cancelUrl("http://localhost:8080")
                .build();
        return new PaymentResponse(invoiceId, payOS.createPaymentLink(paymentData).getQrCode());
    }
}
