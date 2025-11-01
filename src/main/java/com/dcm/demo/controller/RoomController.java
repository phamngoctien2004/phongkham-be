package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<?> getAllRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Integer> departmentIds,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                new ApiResponse<>( roomService.findAll(pageable, keyword, departmentIds), "Fetched all rooms successfully")
        );
    }
}
