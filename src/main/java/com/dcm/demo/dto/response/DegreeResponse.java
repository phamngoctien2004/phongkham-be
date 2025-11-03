package com.dcm.demo.dto.response;

import jakarta.persistence.Column;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DegreeResponse implements Serializable {
    private Integer degreeId;
    private String degreeName;
    private BigDecimal examinationFee;
}
