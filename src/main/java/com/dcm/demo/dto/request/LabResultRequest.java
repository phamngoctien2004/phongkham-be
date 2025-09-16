package com.dcm.demo.dto.request;

import lombok.Data;

@Data
public class LabResultRequest {
    private Integer id;
    private Integer labOrderId;
    private Integer performingDoctorId;
    private String resultDetails;
    private String note;
    private String explanation;
    private String summary;
}
