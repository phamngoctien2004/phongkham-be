package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Báo cáo doanh thu theo thời gian
     * GET /api/reports/revenue?fromDate=2024-01-01&toDate=2024-12-31
     */
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        reportService.getRevenueReport(fromDate, toDate),
                        "Get revenue report successfully"
                )
        );
    }

    /**
     * Báo cáo lịch khám theo bác sĩ, khoa
     * GET /api/reports/appointments?fromDate=2024-01-01&toDate=2024-12-31&doctorId=1&departmentId=1
     */
    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointmentReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) Integer departmentId) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        reportService.getAppointmentReport(fromDate, toDate, doctorId, departmentId),
                        "Get appointment report successfully"
                )
        );
    }

    /**
     * Báo cáo bệnh nhân mới/tái khám
     * GET /api/reports/patients?fromDate=2024-01-01&toDate=2024-12-31
     */
    @GetMapping("/patients")
    public ResponseEntity<?> getPatientReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        reportService.getPatientReport(fromDate, toDate),
                        "Get patient report successfully"
                )
        );
    }

    /**
     * Thống kê hiệu suất bác sĩ
     * GET /api/reports/doctor-performance?fromDate=2024-01-01&toDate=2024-12-31&doctorId=1
     */
    @GetMapping("/doctor-performance")
    public ResponseEntity<?> getDoctorPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer doctorId) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        reportService.getDoctorPerformance(fromDate, toDate, doctorId),
                        "Get doctor performance successfully"
                )
        );
    }

    /**
     * Thống kê dịch vụ phổ biến
     * GET /api/reports/services?fromDate=2024-01-01&toDate=2024-12-31
     */
    @GetMapping("/services")
    public ResponseEntity<?> getServiceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        reportService.getServiceReport(fromDate, toDate),
                        "Get service report successfully"
                )
        );
    }

    /**
     * Xuất báo cáo PDF
     * GET /api/reports/export/pdf?reportType=revenue&fromDate=2024-01-01&toDate=2024-12-31
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<?> exportReportToPdf(
            @RequestParam String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        byte[] pdfData = reportService.exportReportToPdf(reportType, fromDate, toDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    /**
     * Xuất báo cáo Excel
     * GET /api/reports/export/excel?reportType=revenue&fromDate=2024-01-01&toDate=2024-12-31
     */
    @GetMapping("/export/excel")
    public ResponseEntity<?> exportReportToExcel(
            @RequestParam String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        byte[] excelData = reportService.exportReportToExcel(reportType, fromDate, toDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    /**
     * Báo cáo tổng quan - Dashboard
     * GET /api/reports/dashboard?fromDate=2024-01-01&toDate=2024-12-31
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        var revenueData = reportService.getRevenueReport(fromDate, toDate);
        var appointmentsData = reportService.getAppointmentReport(fromDate, toDate, null, null);
        var patientsData = reportService.getPatientReport(fromDate, toDate);
        var servicesData = reportService.getServiceReport(fromDate, toDate);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("revenue", revenueData);
        dashboard.put("appointments", appointmentsData);
        dashboard.put("patients", patientsData);
        dashboard.put("services", servicesData);

        return ResponseEntity.ok(
                new ApiResponse<>(dashboard, "Get dashboard report successfully")
        );
    }
}
