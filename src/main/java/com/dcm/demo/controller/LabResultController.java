package com.dcm.demo.controller;

import com.dcm.demo.dto.request.LabResultRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.LabResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lab-results")
@RequiredArgsConstructor
public class LabResultController {
    private final LabResultService labResultService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LabResultRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(labResultService.createResult(request), "Create lab result successfully")
        );
    }
}
