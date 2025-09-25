package com.dcm.demo.controller;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.service.interfaces.LabOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lab-orders")
@RequiredArgsConstructor
public class LabOrderController {
    private final LabOrderService labOrderService;

    @GetMapping

    @GetMapping("/{code}")
    public ResponseEntity<?> getByRecordCode(@PathVariable String code) {
        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.findByRecordCode(code), "Get lab order by record code successfully")
        );
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody List<LabOrderRequest> request) {
        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.createLabOrder(request), "Create lab order successfully")
        );
    }
}
