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


    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByRecordCode(@PathVariable String code) {
        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.findByRecordCode(code), "Get lab order by record code successfully")
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.findResponseById(id), "Get lab order by id successfully")
        );
    }
    @GetMapping
    public ResponseEntity<?> getAll() {

        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.getAll(), "Get all lab orders successfully")
        );
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody LabOrderRequest request) {
        labOrderService.createLabOrder(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Create lab order successfully")
        );
    }
    @PutMapping
    public ResponseEntity<?> update(@RequestBody LabOrderRequest request) {
        labOrderService.updateLabOrder(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Update lab order successfully")
        );
    }
}
