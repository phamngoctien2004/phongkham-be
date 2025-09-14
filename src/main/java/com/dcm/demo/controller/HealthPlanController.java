package com.dcm.demo.controller;

import com.dcm.demo.service.interfaces.HealthPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class HealthPlanController {
    private final HealthPlanService healthPlanService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(healthPlanService.getAllService());
    }
}
