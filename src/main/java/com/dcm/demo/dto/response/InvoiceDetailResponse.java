package com.dcm.demo.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDetailResponse {
    private Integer id;
    private Integer healthPlanId;
    private String healthPlanName;
    private Integer healthPlanPrice;
    private Integer paid;
    private String paymentMethod;
    private String description;
    private String status;
    private List<LabDetail> multipleLab;
    private LabDetail singleLab;
    private String typeService;
    @Data
    public static class LabDetail {
        private Integer id;
        private String code;
        private String name;
        private String doctorPerforming;
        private String room;
        private LocalDateTime createdAt;
        private String status;
    }
}
