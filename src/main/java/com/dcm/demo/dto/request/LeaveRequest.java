package com.dcm.demo.dto.request;

import com.dcm.demo.model.Leave;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LeaveRequest {
    private Integer id;
    private Integer doctorId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate day;
    private String reason;
    private List<Leave.type> shifts; // ca nghi sang, chieu , toi
    private Leave.leaveStatus leaveStatus;
}
