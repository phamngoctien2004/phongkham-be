package com.dcm.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPerformanceResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<DoctorPerformance> doctorPerformances;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorPerformance {
        private Integer doctorId;
        private String doctorName;
        private String departmentName;
        private Integer totalAppointments;
        private Integer completedAppointments;
        private Integer cancelledAppointments;
        private Integer totalPatients;
        private BigDecimal totalRevenue;
        private Double completionRate;
        private Double averageRating;
        private Integer totalRatings;
    }
}

