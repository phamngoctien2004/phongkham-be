package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.ParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/params")
@RequiredArgsConstructor
public class ParamController {
    private final ParamService paramService;

    @GetMapping
    ResponseEntity<?> getParams(@RequestParam (required = false) String keyword) {
        return ResponseEntity.ok(
                new ApiResponse<>( paramService.findAll(keyword), "Fetched all parameters successfully")
        );
    }
}
