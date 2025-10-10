package com.dcm.demo.controller;

import com.dcm.demo.dto.request.LeaveRequest;
import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.Leave;
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
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) Schedule.Shift shift
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(scheduleService.filterSchedules(
                        departmentId,
                        doctorId,
                        startDate,
                        endDate,
                        shift
                ), "get available slots success")
        );
    }
    @GetMapping("leave/me")
    public ResponseEntity<?> getMyLeaves(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) Leave.leaveStatus status
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(scheduleService.getLeaveByDoctor(date, status), "get my leaves success")
        );
    }
    @PostMapping("/leave")
    public ResponseEntity<?> createLeave(@RequestBody LeaveRequest request) {
        scheduleService.createLeave(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "create leave success")
        );
    }
    @PutMapping("/leave")
    public ResponseEntity<?> updateSchedule(@RequestBody LeaveRequest request) {
        scheduleService.updateLeave(request);
        return ResponseEntity.ok(
                new ApiResponse<>("", "update schedule success")
        );
    }
    @DeleteMapping("/leave")
    public ResponseEntity<?> deleteLeave(@RequestBody LeaveRequest request) {
        scheduleService.deleteLeave(request.getId());
        return ResponseEntity.ok(
                new ApiResponse<>("", "delete leave success")
        );
    }



}
