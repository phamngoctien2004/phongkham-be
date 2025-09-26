package com.dcm.demo.controller;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.FileService;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-record")
public class MedicalController {
    private final MedicalRecordService medicalRecordService;
    private final FileService fileService;

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
        medicalRecordService.create(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "successfully")
        );
    }

    @PutMapping
    public ResponseEntity<?> updateMedicalRecord(@RequestBody MedicalRequest request) {
        medicalRecordService.update(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "successfully")
        );
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> getMedicalRecordPdf(@PathVariable Integer id) {

        byte[] pdf = medicalRecordService.exportPdf(id);

        HttpHeaders headers = new HttpHeaders();
        var cd = ContentDisposition.attachment().filename("invoice-" + id + ".pdf").build();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(cd);
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}