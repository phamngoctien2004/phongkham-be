package com.dcm.demo.controller;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.request.VerifyOtpRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;
    @PostMapping("")
    public ResponseEntity<?> createPatient(@RequestBody PatientRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(patientService.create(request), "Create patient successfully")
        );
    }

    @GetMapping
    public ResponseEntity<?> getPatients(@RequestParam(required = false) String keyword,
                                         @RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Pageable pageable = Pageable.ofSize(limit).withPage(page-1);
        return ResponseEntity.ok(
                new ApiResponse<>(patientService.findAll(keyword, pageable), "Find patient successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(patientService.findById(id), "Find patient successfully")
        );
    }
    @PutMapping
    public ResponseEntity<?> updatePatient(@RequestBody PatientRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(patientService.update(request), "Update patient successfully")
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Integer id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/me")
    public ResponseEntity<?> getMyPatient() {
        return ResponseEntity.ok(
                new ApiResponse<>(patientService.me(), "Get my patient successfully")
        );
    }
    @GetMapping("/relationships")
    public ResponseEntity<?> getAllPatients() {
        return ResponseEntity.ok(
                new ApiResponse<>(patientService.all(), "Get all patients successfully")
        );
    }
    @PostMapping("/relationships")
    public ResponseEntity<?> addRelationship(@RequestBody PatientRequest request) {
        patientService.addRelationship(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Add relationship successfully")
        );
    }
    @PostMapping("/relationships/verify")
    public ResponseEntity<?> verifyRelationship(@RequestBody VerifyOtpRequest request) {
        patientService.syncRelationship(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Verify relationship successfully")
        );
    }
    @DeleteMapping("/relationships/{patientId}")
    public ResponseEntity<?> deleteRelationship(@PathVariable Integer patientId) {
        patientService.deleteRelationship(patientId);
        return ResponseEntity.noContent().build();
    }
}
