package com.dcm.demo.controller;

import com.dcm.demo.dto.request.AcceptAppointmentRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/receptionists")
@RequiredArgsConstructor
public class ReceptionistController {
    private final AppointmentService appointmentService;

    @PostMapping("/confirm")
    public ResponseEntity<?> changeStatusAppointment(@RequestBody AcceptAppointmentRequest request) {
        String message = appointmentService.changeStatusAppointment(request.getId(), request.getStatus());
        return ResponseEntity.ok(
                new ApiResponse<>("", message)
        );
    }
}
