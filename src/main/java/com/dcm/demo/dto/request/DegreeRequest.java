package com.dcm.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DegreeRequest {

    private Integer degreeId;

    private String degreeName;

    private BigDecimal examinationFee;
}
