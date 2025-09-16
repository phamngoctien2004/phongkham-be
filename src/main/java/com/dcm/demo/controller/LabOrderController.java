package com.dcm.demo.controller;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.service.interfaces.LabOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lab-orders")
@RequiredArgsConstructor
public class LabOrderController {
    private final LabOrderService labOrderService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody List<LabOrderRequest> request) {
        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.createLabOrder(request), "Create lab order successfully")
        );
    }
}
