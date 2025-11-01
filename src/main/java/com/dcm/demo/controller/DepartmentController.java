package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    private ResponseEntity<?> getAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/admin")
    private ResponseEntity<?> getAdmin(
            @PageableDefault(size = 10)
            Pageable pageable,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(
                new ApiResponse<>(departmentService.getAllDepartment(pageable, keyword), "Fetched all departments for admin successfully")
        );
    }

    @GetMapping("/{id}/doctors")
    private ResponseEntity<?> getDoctorsByDepartment(@PathVariable Integer id) {
        return ResponseEntity.ok(departmentService.findDoctorByDepartments(id));
    }
}
