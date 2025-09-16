package com.dcm.demo.dto.response;

import com.dcm.demo.model.LabOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LabOrderResponse {
    private Integer id;
    private Integer recordId;
    private HealthPlanResponse healthPlanResponse;
    private String doctorPerformed;
    private String doctorOrdered;
    private LabOrder.TestStatus status;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime expectedResultDate;
}
