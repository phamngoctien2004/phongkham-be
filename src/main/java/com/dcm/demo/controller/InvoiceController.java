package com.dcm.demo.controller;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.request.PaymentRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final MedicalRecordService medicalRecordService;

    @PostMapping("/pay-cash")
    public ResponseEntity<? > payInvoiceCash(@RequestBody PaymentRequest request) {
        medicalRecordService.updateMedicalRecordInvoiceForCash(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Payment successful")
        );
    }
}
