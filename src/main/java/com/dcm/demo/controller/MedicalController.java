package com.dcm.demo.controller;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-record")
public class MedicalController {
    private final MedicalRecordService medicalRecordService;


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
    public ResponseEntity<?> createMedicalRecord(@RequestBody MedicalRequest request){
        medicalRecordService.create(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "successfully")
        );
    }
    @PutMapping
    public ResponseEntity<?> updateMedicalRecord(@RequestBody MedicalRequest request){
        medicalRecordService.update(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "successfully")
        );
    }
}
