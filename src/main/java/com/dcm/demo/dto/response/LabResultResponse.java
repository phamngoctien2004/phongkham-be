package com.dcm.demo.dto.response;

import com.dcm.demo.model.LabResult;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LabResultResponse {
    private Integer id;
    private LocalDateTime date;
    private LabResult.Status status;
    private String resultDetails;
    private String note;
    private String explanation;
}
