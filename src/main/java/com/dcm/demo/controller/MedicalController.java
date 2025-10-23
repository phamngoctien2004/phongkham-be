package com.dcm.demo.controller;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.model.MedicalRecord;
import com.dcm.demo.service.interfaces.FileService;
import com.dcm.demo.service.interfaces.LabOrderService;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-record")
public class MedicalController {
    private final MedicalRecordService medicalRecordService;
    private final LabOrderService labOrderService;
    private final FileService fileService;
    private final TemplateEngine templateEngine;

    @GetMapping("")
    public ResponseEntity<?> getAllMedicalRecord(@RequestParam(name = "keyword",required = false) String keyword,
                                                 @RequestParam(name = "date", required = false) LocalDate date,
                                                 @RequestParam(name = "status", required = false) MedicalRecord.RecordStatus status,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        Pageable pageable = Pageable.ofSize(limit).withPage(page-1);

        return ResponseEntity.ok(
                new ApiResponse<>(medicalRecordService.findAll(keyword, status, date, pageable), "Get all medical record successfully")
        );
    }
    @GetMapping("/doctor")
    public ResponseEntity<?> getAllByDoctor(@RequestParam(name = "keyword",required = false) String keyword,
                                                 @RequestParam(name = "date", required = false) LocalDate date,
                                                 @RequestParam(name = "status", required = false) MedicalRecord.RecordStatus status,
                                                 @RequestParam(name = "isAllDepartment", defaultValue = "false") boolean isAllDepartment,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        Pageable pageable = Pageable.ofSize(limit).withPage(page-1);

        return ResponseEntity.ok(
                new ApiResponse<>(medicalRecordService.findAllByDoctor(keyword, status, date, isAllDepartment, pageable), "Get all medical record successfully")
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicalRecordById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(medicalRecordService.getDetailById(id), "Get medical record by id successfully")
        );
    }
    @GetMapping("/patient/{id}")
    public ResponseEntity<?> getMedicalRecordsByPatientId(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(medicalRecordService.findByPatientId(id), "Get medical record by id successfully")
        );
    }
    @GetMapping("/me")
    public ResponseEntity<?> getMyMedicalRecord() {
        return ResponseEntity.ok(
                new ApiResponse<>(medicalRecordService.me(), "Get my medical record successfully")
        );
    }

    @GetMapping("/me/{cccd}")
    public ResponseEntity<?> getMedicalRecordByCccd(@PathVariable String cccd) {
        return ResponseEntity.ok(
                new ApiResponse<>(medicalRecordService.getRelationMedicalRecord(cccd), "Get medical record by cccd successfully")
        );
    }

    @PostMapping
    public ResponseEntity<?> createMedicalRecord(@RequestBody MedicalRequest request) {
        MedicalRecord medicalRecord = medicalRecordService.create(request);
        labOrderService.createLabOrderFromHealthPlan(medicalRecord, request.getHealthPlanId());
        return ResponseEntity.ok(
                new ApiResponse<>(medicalRecord.getId(), "successfully")
        );
    }

    @PutMapping
    public ResponseEntity<?> updateMedicalRecord(@RequestBody MedicalRequest request) {
        medicalRecordService.update(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "successfully")
        );
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody MedicalRequest request){
        medicalRecordService.updateStatus(request.getId(), request.getStatus());
        return ResponseEntity.ok(
                new ApiResponse<>("", "successfully")
        );
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<?> getMedicalRecordByInvoiceId(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(medicalRecordService.getInvoiceByMedicalRecordId(id), "Get invoice by medical record id successfully")
        );
    }
}