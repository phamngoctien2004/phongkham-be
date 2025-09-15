package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.ScheduleResponse;
import com.dcm.demo.dto.response.SlotResponse;
import com.dcm.demo.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {

    ScheduleResponse create(ScheduleRequest scheduleRequest);

    void delete(Integer id);

    void acceptLeave(Integer id);

    List<SlotResponse> filterSchedules(Integer departmentId, Integer doctorId, LocalDate startDate, LocalDate endDate, Schedule.Shift shift);
}
