package com.dcm.demo.dto.response;

import com.dcm.demo.model.LabResultDetail;
import lombok.Data;

@Data
public class ResultDetailResponse {
    private Integer id;
    private String name;
    private String value;
    private String unit;
    private String range;
    private LabResultDetail.RangeStatus rangeStatus;
}
