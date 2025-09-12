package com.dcm.demo.controller;

import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(scheduleService.create(request), "create schedule success")
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        scheduleService.delete(id);
        return ResponseEntity.ok(
                new ApiResponse<>("", "delete schedule success")
        );
    }


//  lich lam chinh thuc
    @PostMapping("/generate-schedule")
    public ResponseEntity<?> generate(@RequestParam String day) {
        scheduleService.generateScheduleOfficial(java.time.LocalDate.parse(day));
        return ResponseEntity.ok(
                new ApiResponse<>("", "generate schedule official success")
        );
    }
    @PutMapping("/add-exception/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Integer id) {
        scheduleService.updateScheduleOfficial(id);
        return ResponseEntity.ok(
                new ApiResponse<>("", "update schedule success")
        );
    }

}
