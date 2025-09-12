package com.dcm.demo.dto.request;

import com.dcm.demo.model.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ScheduleRequest {
    private Integer id;
    private Integer doctorId;
    private Schedule.DayOfWeek day;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
}
