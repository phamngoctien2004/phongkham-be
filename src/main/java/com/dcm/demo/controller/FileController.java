package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
        return ResponseEntity.ok(
                new ApiResponse<>(fileService.upload(file, type), "Upload file successfully")
        );
    }
    @PostMapping("/multiple")
    public ResponseEntity<?> uploadMultipleFile(@RequestParam("files") MultipartFile[] files, @RequestParam("type") String type) {
        return ResponseEntity.ok(
                new ApiResponse<>(fileService.saveFile(files, type), "Upload files successfully")
        );
    }
}
