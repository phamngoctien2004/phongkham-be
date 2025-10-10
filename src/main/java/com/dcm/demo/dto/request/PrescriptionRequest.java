package com.dcm.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequest {
    private Integer id;
    private String code;
    private Integer medicalRecordId;
    private String generalInstructions;
    private List<DetailRequest> detailRequests;

    @Data
    public static class DetailRequest {
        private Integer medicineId;
        private Integer quantity;
        private String usageInstructions;
    }
}
