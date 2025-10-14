package com.dcm.demo.controller;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.Appointment;
import com.dcm.demo.service.interfaces.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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


    @GetMapping("")
    public ResponseEntity<?> getAppointmentsByPhone(@RequestParam(value = "phone", required = false) String filter,
                                                    @RequestParam(value = "date", required = false) LocalDate date,
                                                    @RequestParam(value = "status", required = false) Appointment.AppointmentStatus status,
                                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                    @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Pageable pageable = Pageable.ofSize(limit).withPage(page-1);
        return ResponseEntity.ok(
                new ApiResponse<>(appointmentService.findByPhone(filter, date, status, pageable), "success")
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
