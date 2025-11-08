package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.LeaveRequest;
import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.LeaveResponse;
import com.dcm.demo.dto.response.ScheduleResponse;
import com.dcm.demo.dto.response.SlotResponse;
import com.dcm.demo.model.Leave;
import com.dcm.demo.model.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleService {
    List<LeaveResponse> getLeaveByDoctor(LocalDate date, Leave.leaveStatus leaveStatus);
    List<LeaveResponse> getLeaveByDoctorId(Integer doctorId, LocalDate date, Leave.leaveStatus leaveStatus);

    ScheduleResponse create(ScheduleRequest scheduleRequest);

    void createLeave(LeaveRequest scheduleRequest);

    void deleteLeave(Integer id);

    void delete(Integer id);

    void updateLeave(LeaveRequest request);

    Schedule.Shift getShift(LocalTime time);

    List<SlotResponse> filterSchedules(Integer departmentId, Integer doctorId, LocalDate startDate, LocalDate endDate, Schedule.Shift shift);
}
