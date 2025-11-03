package com.dcm.demo.controller;

import com.dcm.demo.dto.request.DoctorRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.impl.MeService;
import com.dcm.demo.service.interfaces.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final MeService meService;

    @GetMapping
    public ResponseEntity<?> getAllDoctors() {
        return ResponseEntity.ok(
                new ApiResponse<>(doctorService.findAll(), "Fetched all doctors successfully")
        );
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllDoctorsForAdmin(@RequestParam(name = "keyword", required = false) String keyword,
                                                   @RequestParam(name = "departmentId", required = false) Integer departmentId,
                                                   @RequestParam(name = "degreeId", required = false) Integer degreeId,
                                                   @PageableDefault(size = 10)
                                                   Pageable pageable) {
        return ResponseEntity.ok(
                new ApiResponse<>(doctorService.findAllByPage(pageable, keyword, departmentId, degreeId), "Fetched all doctors for admin successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(doctorService.findResponseById(id), "Fetched doctor successfully")
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo() {
        return ResponseEntity.ok(
                new ApiResponse<>(meService.me(), "Fetched my info successfully")
        );
    }

    @PostMapping
    public ResponseEntity<?> createDoctor(@RequestBody DoctorRequest doctorRequest) {
        return ResponseEntity.ok(
                new ApiResponse<>(doctorService.create(doctorRequest), "Created doctor successfully")
        );
    }
    @PutMapping
    public ResponseEntity<?> updateDoctor(@RequestBody DoctorRequest doctorRequest) {
        return ResponseEntity.ok(
                new ApiResponse<>(doctorService.update(doctorRequest), "Updated doctor successfully")
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer id) {
        doctorService.delete(id);
        return ResponseEntity.ok(
                new ApiResponse<>("", "Deleted doctor successfully")
        );
    }
}
