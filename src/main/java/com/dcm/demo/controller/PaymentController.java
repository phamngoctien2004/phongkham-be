package com.dcm.demo.controller;

import com.dcm.demo.dto.request.PaymentRequest;
import com.dcm.demo.dto.request.WebhookRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.impl.PayosService;
import com.dcm.demo.service.interfaces.InvoiceService;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PayosService payosService;
    private final InvoiceService invoiceService;
    private final MedicalRecordService medicalRecordService;

    @PostMapping("/create-link")
    public ApiResponse<?> createPaymentLink(@RequestBody PaymentRequest paymentRequest) {
        // Logic to create a payment link
        return new ApiResponse<>(payosService.createLinkPayment(paymentRequest), "Payment link created successfully");
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody WebhookRequest webhookRequest) {
        // Logic to handle webhook notifications
//        medicalRecordService.webhookPayosForCheckStatus(webhookRequest);
    }

    //     loi o day nhe
    @GetMapping("/status/{orderCode}")
    public ApiResponse<?> getPaymentStatus(@PathVariable Long orderCode) {
        // Logic to get payment status
        System.out.println("ok");
        return new ApiResponse<>(payosService.checkStatus(orderCode), "Payment status retrieved successfully");
    }

    @PostMapping("/appointment/{id}")
    public ApiResponse<?> createLinkAppointment(@PathVariable Integer id) {
        return new ApiResponse<>(payosService.createLinkAppointment(id), "Payment link created successfully");
    }
}
