package com.dcm.demo.controller;

import com.dcm.demo.dto.request.PresDetailRequest;
import com.dcm.demo.dto.request.PrescriptionRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.Prescription;
import com.dcm.demo.service.interfaces.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @GetMapping("/medical-record/{id}")
    public ResponseEntity<?> getByMedicalRecordId(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(prescriptionService.getPrescriptionsByMedicalRecordId(id), "Fetch prescriptions successfully")
        );
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PrescriptionRequest prescription) {
        return ResponseEntity.ok(
                new ApiResponse<>(prescriptionService.create(prescription), "Create prescription successfully")
        );
    }
    @PutMapping
    public ResponseEntity<?> update(@RequestBody PrescriptionRequest prescription) {
        return ResponseEntity.ok(
                new ApiResponse<>(prescriptionService.update(prescription), "Update prescription successfully")
        );
    }

    @PostMapping("/details")
    public ResponseEntity<?> createPreDetail(@RequestBody PresDetailRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(prescriptionService.createPreDetail(request), "Create prescription detail successfully")
        );
    }
    @PutMapping("/details")
    public ResponseEntity<?> updatePreDetail(@RequestBody PresDetailRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(prescriptionService.updatePreDetail(request), "Update prescription detail successfully")
        );
    }
    @DeleteMapping("/details/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        prescriptionService.deletePreDetail(id);
        return ResponseEntity.noContent().build();
    }
}
