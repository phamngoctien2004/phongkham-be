package com.dcm.demo.controller;

import com.dcm.demo.dto.request.AcceptAppointmentRequest;
import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.AppointmentService;
import com.dcm.demo.service.interfaces.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receptionists")
@RequiredArgsConstructor
public class ReceptionistController {
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    @PostMapping("/confirm")
    public ResponseEntity<?> changeStatusAppointment(@RequestBody AcceptAppointmentRequest request) {
        String message = appointmentService.changeStatusAppointment(request.getId(), request.getStatus());
        return ResponseEntity.ok(
                new ApiResponse<>("", message)
        );
    }

    @PostMapping("/patient")
    public ResponseEntity<?> createPatient(@RequestBody PatientRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(patientService.create(request), "Create patient successfully")
        );
    }
    @GetMapping("/patient")
    public ResponseEntity<?> getPatientsByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(
                new ApiResponse<>(patientService.findAllPatientByPhone(phone), "Find patient successfully")
        );
    }
}
