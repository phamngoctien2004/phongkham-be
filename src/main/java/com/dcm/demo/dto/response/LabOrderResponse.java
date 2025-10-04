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
    private String code;
    private Integer recordId;
    private Integer healthPlanId;
    private String healthPlanName;
    private String room;
    private String doctorPerformed;
    private Integer doctorPerformedId;
    private String doctorOrdered;
    private LabOrder.TestStatus status;
    private String statusPayment;
    private BigDecimal price;
    private LocalDateTime orderDate;
    private String diagnosis;
    private LocalDateTime expectedResultDate;
    private String serviceParent;
    private LabResultResponse labResultResponse;
}
