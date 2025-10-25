package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.HealthPlan;
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
    public ResponseEntity<?> getAll(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) HealthPlan.ServiceType type) {
        return ResponseEntity.ok(healthPlanService.getAllService(keyword, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(healthPlanService.findResponseById(id));
    }

    @GetMapping("/optional")
    public ResponseEntity<?> getOptional() {
        return ResponseEntity.ok(
                new ApiResponse<>(healthPlanService.displayClientHealthPlans() , "Fetched optional services successfully")
        );
    }

    @GetMapping("/optional/{id}")
    public ResponseEntity<?> getOptionalById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(healthPlanService.findDetail(id) , "Fetched service detail successfully")
        );
    }
}
