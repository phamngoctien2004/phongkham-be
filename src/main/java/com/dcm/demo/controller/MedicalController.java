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
