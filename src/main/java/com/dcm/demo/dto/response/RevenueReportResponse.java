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
public class RevenueReportResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalRevenue;
    private BigDecimal totalPaid;
    private BigDecimal totalUnpaid;
    private Integer totalInvoices;
    private Integer totalPaidInvoices;
    private Integer totalUnpaidInvoices;
    private List<RevenueByDay> revenueByDays;
    private List<RevenueByPaymentMethod> revenueByPaymentMethods;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueByDay {
        private LocalDate date;
        private BigDecimal revenue;
        private Integer invoiceCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueByPaymentMethod {
        private String paymentMethod;
        private BigDecimal amount;
        private Integer count;
    }
}

