package com.dcm.demo.controller;

import com.dcm.demo.dto.request.LabDeleteRequest;
import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.service.interfaces.LabOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lab-orders")
public class LabOrderController {
    private final LabOrderService labOrderService;


    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByRecordCode(@PathVariable String code) {
        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.findByRecordCode(code), "Get lab order by record code successfully")
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.findResponseById(id), "Get lab order by id successfully")
        );
    }
    @GetMapping("/processing/{id}")
    public ResponseEntity<?> getByPerformDoctorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.fetchAndMarkProcessingLabOrder(id), "Get lab order successfully")
        );
    }
    @GetMapping
    public ResponseEntity<?> getAll() {

        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.getAll(), "Get all lab orders successfully")
        );
    }
    @GetMapping("/doctor")
    public ResponseEntity<?> getAllByDoctor(@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) LabOrder.TestStatus status,
                                            @RequestParam(required = false) LocalDate date,
                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Pageable pageable = Pageable.ofSize(limit).withPage(page - 1);

        return ResponseEntity.ok(
                new ApiResponse<>(labOrderService.getByDoctorPerforming(keyword, date, status, pageable), "Get all lab orders of doctor successfully")
        );
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody LabOrderRequest request) {
        labOrderService.createLabOrder(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Create lab order successfully")
        );
    }
    @PutMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody LabOrderRequest request) {
        labOrderService.updateStatus(request.getId(), request.getStatus());
        return ResponseEntity.ok(
                new ApiResponse<>("", "Update lab order status successfully")
        );
    }
    @PutMapping
    public ResponseEntity<?> update(@RequestBody LabOrderRequest request) {
        labOrderService.updateLabOrder(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Update lab order successfully")
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        labOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
