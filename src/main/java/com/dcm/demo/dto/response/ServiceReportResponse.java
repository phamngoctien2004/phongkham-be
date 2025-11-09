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
public class ServiceReportResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer totalServices;
    private List<PopularService> popularServices;
    private List<ServiceByDepartment> servicesByDepartment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PopularService {
        private Integer serviceId;
        private String serviceName;
        private Integer usageCount;
        private BigDecimal totalRevenue;
        private BigDecimal price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServiceByDepartment {
        private Integer departmentId;
        private String departmentName;
        private Integer serviceCount;
        private Integer usageCount;
        private BigDecimal totalRevenue;
    }
}
