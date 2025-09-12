package com.dcm.demo.dto.response;

import com.dcm.demo.model.Schedule;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ScheduleResponse {
    private Integer id;
    private DoctorResponse doctor;
    private Schedule.DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
}
