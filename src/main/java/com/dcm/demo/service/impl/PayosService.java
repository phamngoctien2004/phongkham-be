package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.request.PaymentRequest;
import com.dcm.demo.dto.response.InvoiceResponse;
import com.dcm.demo.dto.response.PaymentResponse;
import com.dcm.demo.dto.response.PayosResponse;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.model.MedicalRecord;
import com.dcm.demo.service.interfaces.AppointmentService;
import com.dcm.demo.service.interfaces.InvoiceService;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayosService {
    private final String apiKey = "4b77777d-dc01-4aa4-856c-873414b23ae3";
    private final String clientId = "cc203012-d56d-4dc7-bccc-4894befa5591";
    private final String checksumKey = "876c6d0687b6fbb15463f2c522511f8c572c8d68918b16dbc46bd4301584889f";

    private final InvoiceService invoiceService;
    private final MedicalRecordService medicalRecordService;
    private final AppointmentService appointmentService;
    final PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

    public PaymentResponse createLinkPayment(PaymentRequest paymentRequest) {

        try {
            if (paymentRequest.getMedicalRecordId() == null || paymentRequest.getMedicalRecordId() == 0) {
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
        CreatePaymentLinkRequest req = CreatePaymentLinkRequest
                .builder()
                .orderCode(orderCode)
                .amount((long)invoiceResponse.getExamFee())
                .description("Thanh toán hoa đơn ")
                .returnUrl("http://localhost:8080")
                .cancelUrl("http://localhost:8080")
                .build();
        CreatePaymentLinkResponse res = payOS.paymentRequests().create(req);;
        return new PaymentResponse(invoiceResponse.getId(), res.getQrCode(), orderCode);
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

        CreatePaymentLinkRequest req = CreatePaymentLinkRequest
                .builder()
                .orderCode(orderCode)
                .amount(total.longValue())
                .description(description)
                .returnUrl("http://localhost:8080")
                .cancelUrl("http://localhost:8080")
                .build();
        CreatePaymentLinkResponse res = payOS.paymentRequests().create(req);;

        return new PaymentResponse(invoice.getId(), res.getQrCode(), orderCode);
    }

    @Transactional
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

//
//    public PaymentResponse createLinkAppointment(Integer id) {
//        try {
//            Long orderCode = System.currentTimeMillis() / 1000;
//            Appointment appointment = appointmentService.findById(id);
//            int amount = 0;
//            String description = "DLK-" + appointment.getId();
//            InvoiceRequest invoiceRequest = new InvoiceRequest();
//            invoiceRequest.setPaymentMethod(Invoice.PaymentMethod.CHUYEN_KHOAN);
//            invoiceRequest.setOrderCode(orderCode);
//
//            if (appointment.getDoctor() != null) {
//                Doctor doctor = appointment.getDoctor();
//                Degree degree = doctor.getDegree();
//                amount += degree.getExaminationFee().intValue();
//                invoiceRequest.setDoctorId(doctor.getId());//     loi o day nhe
//
//            } else {
//                HealthPlan healthPlan = appointment.getHealthPlan();
//                amount += healthPlan.getPrice().intValue();
//                invoiceRequest.setHealthPlanIds(List.of(healthPlan.getId()));
//            }
//
//            InvoiceResponse invoiceResponse = invoiceService.createInvoiceForQR(invoiceRequest);
//            PaymentData paymentData = PaymentData
//                    .builder()
//                    .orderCode(orderCode)
//                    .amount(amount)
//                    .description(description)
//                    .returnUrl("http://localhost:5173/payment/success")
//                    .cancelUrl("http://localhost:5173/payment/cancel")
//                    .build();
//            CheckoutResponseData responseData = payOS.createPaymentLink(paymentData);
//            System.out.println(responseData.getCheckoutUrl());
//            appointmentService.save(appointment);
//            return new PaymentResponse(invoiceResponse.getId(), responseData.getQrCode(), orderCode);
//        } catch (Exception ignored) {
//            throw new RuntimeException("Error creating payment link for appointment: " + ignored.getMessage());
//        }
//    }
}