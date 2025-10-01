package com.dcm.demo.controller;

import com.dcm.demo.service.interfaces.HealthPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class HealthPlanController {
    private final HealthPlanService healthPlanService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(healthPlanService.getAllService(keyword));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(healthPlanService.findResponseById(id));
    }
}
