package com.dcm.demo.controller;

import com.dcm.demo.dto.request.FilterSlotRequest;
import com.dcm.demo.dto.request.ScheduleExceptionRequest;
import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.Schedule;
import com.dcm.demo.service.interfaces.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("")

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

    @GetMapping("/slots")
    public ResponseEntity<?> filterSlots(
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Schedule.DayOfWeek day
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(scheduleService.filterSlots(doctorId,departmentId,day), "filter slots success")
        );
    }

    @PutMapping("/leave/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Integer id) {
        scheduleService.acceptLeave(id);
        return ResponseEntity.ok(
                new ApiResponse<>("", "update schedule success")
        );
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam  LocalDate startDate,
            @RequestParam  LocalDate endDate,
            @RequestParam Integer departmentId,
            @RequestParam Integer doctorId,
            @RequestParam Schedule.Shift shift
    ) {
        return ResponseEntity.ok(
                "a"
        );
    }
}
