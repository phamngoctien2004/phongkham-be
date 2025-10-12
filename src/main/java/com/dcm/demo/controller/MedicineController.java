package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.dto.response.MedicineResponse;
import com.dcm.demo.repository.MedicineRepository;
import com.dcm.demo.service.interfaces.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineService medicineService;
    @GetMapping
    public ResponseEntity<?> getAllMedicines(@RequestParam (required = false) String keyword) {
        return ResponseEntity.ok(
                new ApiResponse<>(medicineService.getAllMedicines(keyword), "Fetch medicines successfully")
        );
    }
}
