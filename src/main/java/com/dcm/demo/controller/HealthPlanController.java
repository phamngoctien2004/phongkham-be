package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.service.interfaces.HealthPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @GetMapping("/admin")
    public ResponseEntity<?> getAllForAdmin(@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) BigDecimal priceFrom,
                                            @RequestParam(required = false) BigDecimal priceTo,
                                            @RequestParam(required = false)HealthPlan.ServiceType type,
                                            @PageableDefault(size = 10)
                                            Pageable pageable) {
        return ResponseEntity.ok(
                new ApiResponse<>(healthPlanService.getAllService(keyword, priceFrom, priceTo, type, pageable), "Fetched all services for admin successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(healthPlanService.findResponseById(id));
    }

    @GetMapping("/optional")
    public ResponseEntity<?> getOptional(@RequestParam(required = false) HealthPlan.ServiceType type) {
        return ResponseEntity.ok(
                new ApiResponse<>(healthPlanService.displayClientHealthPlans(type), "Fetched optional services successfully")
        );
    }

    @GetMapping("/optional/{id}")
    public ResponseEntity<?> getOptionalById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(healthPlanService.findDetail(id), "Fetched service detail successfully")
        );
    }
}
