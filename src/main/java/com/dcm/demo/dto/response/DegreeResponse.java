package com.dcm.demo.dto.response;

import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DegreeResponse {
    private Integer degreeId;
    private String degreeName;
    private BigDecimal examinationFee;
}
