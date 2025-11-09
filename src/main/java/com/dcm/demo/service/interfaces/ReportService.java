package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.response.*;

import java.time.LocalDate;

public interface ReportService {

    // Báo cáo doanh thu
    RevenueReportResponse getRevenueReport(LocalDate fromDate, LocalDate toDate);

    // Báo cáo lịch khám
    AppointmentReportResponse getAppointmentReport(LocalDate fromDate, LocalDate toDate, Integer doctorId, Integer departmentId);

    // Báo cáo bệnh nhân
    PatientReportResponse getPatientReport(LocalDate fromDate, LocalDate toDate);

    // Thống kê hiệu suất bác sĩ
    DoctorPerformanceResponse getDoctorPerformance(LocalDate fromDate, LocalDate toDate, Integer doctorId);

    // Thống kê dịch vụ
    ServiceReportResponse getServiceReport(LocalDate fromDate, LocalDate toDate);

    // Xuất báo cáo PDF
    byte[] exportReportToPdf(String reportType, LocalDate fromDate, LocalDate toDate);

    // Xuất báo cáo Excel
    byte[] exportReportToExcel(String reportType, LocalDate fromDate, LocalDate toDate);
}
