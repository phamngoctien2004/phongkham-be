package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.ScheduleResponse;

import java.time.LocalDate;

public interface ScheduleService {

    ScheduleResponse create(ScheduleRequest scheduleRequest);
    void delete(Integer id);

    void generateScheduleOfficial(LocalDate day);
    void updateScheduleOfficial(Integer scheduleExceptionId);
}
