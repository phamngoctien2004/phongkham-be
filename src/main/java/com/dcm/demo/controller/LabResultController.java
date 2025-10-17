package com.dcm.demo.controller;

import com.dcm.demo.dto.request.LabResultRequest;
import com.dcm.demo.dto.request.ParamRequest;
import com.dcm.demo.dto.request.ResultDetailRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.LabResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lab-results")
@RequiredArgsConstructor
public class LabResultController {
    private final LabResultService labResultService;

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getParamResults(@PathVariable("id") Integer labResultId) {
        return ResponseEntity.ok(
                new ApiResponse<>(labResultService.getParamResults(labResultId), "Get lab result details successfully")
        );
    }
//    @PostMapping("/details")
//    public ResponseEntity<?> createParamResults(@RequestBody ParamRequest request) {
//        labResultService.createParamResults(request);
//        return ResponseEntity.ok(
//                new ApiResponse<>("","Create lab result details successfully")
//        );
//    }
    @PutMapping("/details")
    public ResponseEntity<?> updateResultDetails(@RequestBody ResultDetailRequest request) {
        labResultService.updateResultDetails(request);
        return ResponseEntity.ok(
                new ApiResponse<>("","Update lab result details successfully")
        );
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody LabResultRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(labResultService.createResult(request), "Create lab result successfully")
        );
    }
    @PutMapping
    public ResponseEntity<?> update(@RequestBody LabResultRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(labResultService.updateResult(request),"Update lab result successfully")
        );
    }


}
