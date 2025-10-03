package com.dcm.demo.controller;

import com.dcm.demo.dto.request.PaymentRequest;
import com.dcm.demo.dto.request.WebhookRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.impl.PayosService;
import com.dcm.demo.service.interfaces.InvoiceService;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public String handleWebhook(@RequestBody WebhookRequest webhookRequest) {
        // Logic to handle webhook notifications
        medicalRecordService.webhookPayosForCheckStatus(webhookRequest);
        return "ok";
    }

//     loi o day nhe
    @GetMapping("/status/{invoiceId}")
    public ApiResponse<?> getPaymentStatus(@PathVariable Integer invoiceId) {
        // Logic to get payment status
        return new ApiResponse<>(invoiceService.checkStatusPayment(invoiceId), "Payment status retrieved successfully");
    }

    @GetMapping("/test")
    public ApiResponse<?> test() {
        // Logic to get payment status
        return new ApiResponse<>(invoiceService.getServicesUnPay(26), "Payment status retrieved successfully");
    }
}
