package com.dcm.demo.controller;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.Appointment;
import com.dcm.demo.service.interfaces.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    @PostMapping("")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        appointmentService.createAppointment(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Appointment booked successfully")
        );
    }

    @GetMapping("/phone")
    public ResponseEntity<?> getAppointmentsByPhone(@RequestParam(value = "phone", required = false) String filter) {
        return ResponseEntity.ok(
                new ApiResponse<>(appointmentService.findByPhone(filter), "success")
        );
    }
    @PutMapping("/confirm")
    public ResponseEntity<?> confirmAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        appointmentService.changeStatusAppointment(appointmentRequest);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Appointment confirmed successfully")
        );
    }
}
