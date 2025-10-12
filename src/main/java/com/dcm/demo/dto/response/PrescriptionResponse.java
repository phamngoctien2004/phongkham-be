package com.dcm.demo.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionResponse {
    private Integer id;
    private String code;
    private String generalInstructions;
    private String doctorCreated;
    private LocalDateTime prescriptionDate;
    private List<PreDetailResponse> detailResponses;
}
