package com.dcm.demo.controller;

import com.dcm.demo.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    private ResponseEntity<?> getAll(){
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}/doctors")
    private ResponseEntity<?> getDoctorsByDepartment(@PathVariable Integer id){
        return ResponseEntity.ok(departmentService.findDoctorByDepartments(id));
    }
}
