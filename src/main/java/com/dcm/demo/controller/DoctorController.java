package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.impl.MeService;
import com.dcm.demo.service.interfaces.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final MeService meService;

    @GetMapping
    public ResponseEntity<?> getAllDoctors() {
        return ResponseEntity.ok(
                new ApiResponse<>(doctorService.findAll(), "Fetched all doctors successfully")
        );
    }
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo() {
        return ResponseEntity.ok(
                new ApiResponse<>(meService.me(), "Fetched my info successfully")
        );
    }
}
