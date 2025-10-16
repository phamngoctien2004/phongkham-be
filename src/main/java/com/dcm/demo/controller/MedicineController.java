package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<?> getAllMedicines(@RequestParam(required = false) String keyword,
                                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Pageable pageable = Pageable.ofSize(limit).withPage(page - 1);
        return ResponseEntity.ok(
                new ApiResponse<>(medicineService.getAllMedicines(keyword, pageable), "Fetch medicines successfully")
        );
    }
}
