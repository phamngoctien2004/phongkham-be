package com.dcm.demo.controller;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.PatientService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> getPatientsByPhone(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(
                new ApiResponse<>(patientService.findAll(keyword), "Find patient successfully")
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
}
