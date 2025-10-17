package com.dcm.demo.dto.response;

import com.dcm.demo.model.LabResultDetail;
import lombok.Data;

@Data
public class ParamResponse {
    private Integer id;
    private String name;
    private String unit;
    private String range;
}
