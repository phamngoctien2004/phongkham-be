package com.dcm.demo.dto.response;

import com.dcm.demo.dto.request.PrescriptionRequest;
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
    private List<DetailResponse> detailResponses;

    @Data
    public static class DetailResponse {
        private Integer prescriptionDetailId;
        private String medicineName;
        private Integer quantity;
        private String usageInstructions;
    }
}
