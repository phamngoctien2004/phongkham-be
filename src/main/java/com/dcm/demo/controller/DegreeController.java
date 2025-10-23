package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.DegreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/degrees")
@RequiredArgsConstructor
public class DegreeController {
    private final DegreeService degreeService;

    @GetMapping
    public ResponseEntity<?> getDegrees() {

        return ResponseEntity.ok(
                new ApiResponse<>(degreeService.getDegrees(), "Fetched all degrees successfully")
        );

    }
}
