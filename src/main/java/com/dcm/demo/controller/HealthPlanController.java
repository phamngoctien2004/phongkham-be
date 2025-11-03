package com.dcm.demo.controller;

import com.dcm.demo.dto.request.HealthPlanRequest;
import com.dcm.demo.dto.request.ParamDTO;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.service.interfaces.HealthPlanService;
import com.dcm.demo.service.interfaces.ParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class HealthPlanController {
    private final HealthPlanService healthPlanService;
    private final ParamService paramService;
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

    @PostMapping
    public ResponseEntity<?> create(@RequestBody HealthPlanRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(healthPlanService.create(request), "Created service successfully")
        );
    }
    @PutMapping
    public ResponseEntity<?> update(@RequestBody HealthPlanRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(healthPlanService.update(request), "Updated service successfully")
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        healthPlanService.delete(id);
        return ResponseEntity.ok(
                new ApiResponse<>(null, "Deleted service successfully")
        );
    }

    @GetMapping("/params/{id}")
    public ResponseEntity<?> getParams(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(healthPlanService.findParamsByServiceId(id), "Fetched parameters successfully")
        );
    }

    @PostMapping("/params")
    public ResponseEntity<?> addParams(@RequestBody ParamDTO request) {
        paramService.addParamInService(request);
        return ResponseEntity.ok(
                new ApiResponse<>(null, "Added parameters to health plan successfully")
        );
    }

    @DeleteMapping("/params")
    public ResponseEntity<?> deleteParams(@RequestBody ParamDTO request) {
        paramService.deleteParamInService(request);
        return ResponseEntity.ok(
                new ApiResponse<>(null, "Deleted parameters from health plan successfully")
        );
    }
}
