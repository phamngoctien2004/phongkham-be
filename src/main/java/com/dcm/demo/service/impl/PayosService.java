package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.request.PaymentRequest;
import com.dcm.demo.dto.response.InvoiceResponse;
import com.dcm.demo.dto.response.PaymentResponse;
import com.dcm.demo.dto.response.PayosResponse;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.model.MedicalRecord;
import com.dcm.demo.service.interfaces.InvoiceService;
import com.dcm.demo.service.interfaces.LabOrderService;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

//      Chon bac si
        if (paymentRequest.getDoctorId() != null) {
            invoiceRequest.setDoctorId(paymentRequest.getDoctorId());
        }
//      dich vu kham
        if (paymentRequest.getHealthPlanIds() != null && !paymentRequest.getHealthPlanIds().isEmpty()) {
            invoiceRequest.setHealthPlanIds(paymentRequest.getHealthPlanIds());
        }
        InvoiceResponse invoiceResponse = invoiceService.createInvoiceForQR(invoiceRequest);
        System.out.println("Invoice ID: " + invoiceResponse.getId());
        PaymentData paymentData = PaymentData
                .builder()
                .orderCode(orderCode)
                .amount(invoiceResponse.getExamFee())
                .description("Thanh toán hoa đơn ")

                .build();
        return new PaymentResponse(invoiceResponse.getId(), payOS.createPaymentLink(paymentData).getQrCode(), orderCode);
    }

    private PaymentResponse createLinkPaymentV2(PaymentRequest paymentRequest) throws Exception {
        Long orderCode = System.currentTimeMillis() / 1000;
        MedicalRecord medicalRecord = medicalRecordService.findById(paymentRequest.getMedicalRecordId());
        Invoice invoice = medicalRecord.getInvoice();

//      danh sach dich vu thanh toan
        List<Integer> servicesSelected = paymentRequest.getHealthPlanIds();

//      tinh tong
        BigDecimal total = invoice.getTotalAmount().subtract(invoice.getPaidAmount());

//      tao mo ta de xu li sau nay
        String description = "T" + medicalRecord.getId() + "X" + servicesSelected
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining("D"));

        PaymentData paymentData = PaymentData
                .builder()
                .orderCode(orderCode)
                .amount(total.intValue())
                .description(description)
                .build();
        return new PaymentResponse(medicalRecord.getId(), payOS.createPaymentLink(paymentData).getQrCode(), orderCode);
    }

    public boolean checkStatus(Long orderCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-client-id", "355718b7-5f38-44ea-b31e-3e269705aa5b");
        headers.add("x-api-key", "06eac658-bada-4cee-92a3-48626b520aab");
        HttpEntity<Void> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        PayosResponse response = restTemplate.exchange(
                "https://api-merchant.payos.vn/v2/payment-requests/" + orderCode,
                HttpMethod.GET,
                request,
                PayosResponse.class).getBody();

        return "PAID".equalsIgnoreCase(response.getData().getStatus());
    }

}
