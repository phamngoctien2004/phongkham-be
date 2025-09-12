package com.dcm.demo.dto.request;

import com.dcm.demo.model.ScheduleException;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleExceptionRequest {
    private Integer id;
    private Integer doctorId;
    private LocalDate day;
    private List<TimeDto> times;
    private String reason;
    private ScheduleException.type type;
    private ScheduleException.leaveStatus leaveStatus;
}
