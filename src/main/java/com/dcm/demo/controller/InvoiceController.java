package com.dcm.demo.controller;

import com.dcm.demo.dto.request.PaymentRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.service.interfaces.InvoiceService;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final MedicalRecordService medicalRecordService;
    private final InvoiceService invoiceService;

    @PostMapping("/pay-cash")
    public ResponseEntity<?> payInvoiceCash(@RequestBody PaymentRequest request) {
        medicalRecordService.updateMedicalRecordInvoiceForCash(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Payment successful")
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @PageableDefault(sort = "paymentDate", direction = Sort.Direction.DESC, size = 10)
            Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Invoice.PaymentStatus paymentStatus,
            @RequestParam(required = false) Invoice.PaymentMethod method,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {


        return ResponseEntity.ok(
                new ApiResponse<>(invoiceService.findAll(keyword, paymentStatus, method, fromDate, toDate, pageable), "Get all medical record successfully")
        );
    }

}
