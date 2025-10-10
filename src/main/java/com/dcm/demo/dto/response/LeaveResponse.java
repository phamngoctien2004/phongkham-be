package com.dcm.demo.dto.response;

import com.dcm.demo.model.Leave;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class LeaveResponse {
    private Integer id;
    private String doctorName;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private LocalDate submitDate;
    private String reason;
    private Leave.leaveStatus leaveStatus;
    private String userApprover;
}
