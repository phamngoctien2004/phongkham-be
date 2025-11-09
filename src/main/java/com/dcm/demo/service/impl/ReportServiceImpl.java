package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.*;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.repository.*;
import com.dcm.demo.service.interfaces.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final InvoiceRepository invoiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ExaminationServiceRepository examinationServiceRepository;
    private final PdfService pdfService;

    @Override
    public RevenueReportResponse getRevenueReport(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        // Tổng doanh thu
        BigDecimal totalRevenue = invoiceRepository.getTotalRevenue(fromDateTime, toDateTime);
        if (totalRevenue == null)
            totalRevenue = BigDecimal.ZERO;

        // Đếm hóa đơn theo trạng thái
        Long paidInvoices = invoiceRepository.countByStatusAndDateRange(fromDateTime, toDateTime,
                Invoice.PaymentStatus.DA_THANH_TOAN);
        Long unpaidInvoices = invoiceRepository.countByStatusAndDateRange(fromDateTime, toDateTime,
                Invoice.PaymentStatus.CHUA_THANH_TOAN);

        // Doanh thu theo ngày
        List<Object[]> revenueByDayData = invoiceRepository.getRevenueByDay(fromDateTime, toDateTime);
        List<RevenueReportResponse.RevenueByDay> revenueByDays = revenueByDayData.stream()
                .map(row -> {
                    // Convert java.sql.Date to LocalDate if needed
                    LocalDate date = row[0] instanceof java.sql.Date
                            ? ((java.sql.Date) row[0]).toLocalDate()
                            : (LocalDate) row[0];
                    return RevenueReportResponse.RevenueByDay.builder()
                            .date(date)
                            .revenue((BigDecimal) row[1])
                            .invoiceCount(((Number) row[2]).intValue())
                            .build();
                })
                .collect(Collectors.toList());

        // Doanh thu theo phương thức thanh toán
        List<Object[]> revenueByMethodData = invoiceRepository.getRevenueByPaymentMethod(fromDateTime, toDateTime);
        List<RevenueReportResponse.RevenueByPaymentMethod> revenueByPaymentMethods = revenueByMethodData.stream()
                .map(row -> RevenueReportResponse.RevenueByPaymentMethod.builder()
                        .paymentMethod(row[0] != null ? row[0].toString() : "N/A")
                        .amount((BigDecimal) row[1])
                        .count(((Number) row[2]).intValue())
                        .build())
                .collect(Collectors.toList());

        return RevenueReportResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalRevenue(totalRevenue)
                .totalPaid(totalRevenue)
                .totalUnpaid(BigDecimal.ZERO)
                .totalInvoices((int) (paidInvoices + unpaidInvoices))
                .totalPaidInvoices(paidInvoices.intValue())
                .totalUnpaidInvoices(unpaidInvoices.intValue())
                .revenueByDays(revenueByDays)
                .revenueByPaymentMethods(revenueByPaymentMethods)
                .build();
    }

    @Override
    public AppointmentReportResponse getAppointmentReport(LocalDate fromDate, LocalDate toDate, Integer doctorId,
            Integer departmentId) {
        // Thống kê tổng quan
        Long totalAppointments = appointmentRepository.countByDateRangeAndStatus(fromDate, toDate, null);
        Long confirmedAppointments = appointmentRepository.countByDateRangeAndStatus(fromDate, toDate,
                com.dcm.demo.model.Appointment.AppointmentStatus.DA_XAC_NHAN);
        Long completedAppointments = appointmentRepository.countByDateRangeAndStatus(fromDate, toDate,
                com.dcm.demo.model.Appointment.AppointmentStatus.HOAN_THANH);
        Long cancelledAppointments = appointmentRepository.countByDateRangeAndStatus(fromDate, toDate,
                com.dcm.demo.model.Appointment.AppointmentStatus.HUY);

        // Lịch khám theo bác sĩ
        List<Object[]> byDoctorData = appointmentRepository.getAppointmentsByDoctor(fromDate, toDate);
        List<AppointmentReportResponse.AppointmentByDoctor> appointmentsByDoctor = byDoctorData.stream()
                .filter(row -> doctorId == null || ((Number) row[0]).intValue() == doctorId)
                .map(row -> AppointmentReportResponse.AppointmentByDoctor.builder()
                        .doctorId(((Number) row[0]).intValue())
                        .doctorName((String) row[1])
                        .departmentName((String) row[2])
                        .totalAppointments(((Number) row[3]).intValue())
                        .completedAppointments(((Number) row[4]).intValue())
                        .cancelledAppointments(((Number) row[5]).intValue())
                        .build())
                .collect(Collectors.toList());

        // Lịch khám theo khoa
        List<Object[]> byDepartmentData = appointmentRepository.getAppointmentsByDepartment(fromDate, toDate);
        List<AppointmentReportResponse.AppointmentByDepartment> appointmentsByDepartment = byDepartmentData.stream()
                .filter(row -> departmentId == null || ((Number) row[0]).intValue() == departmentId)
                .map(row -> AppointmentReportResponse.AppointmentByDepartment.builder()
                        .departmentId(((Number) row[0]).intValue())
                        .departmentName((String) row[1])
                        .totalAppointments(((Number) row[2]).intValue())
                        .completedAppointments(((Number) row[3]).intValue())
                        .build())
                .collect(Collectors.toList());

        // Lịch khám theo ngày
        List<Object[]> byDayData = appointmentRepository.getAppointmentsByDay(fromDate, toDate);
        List<AppointmentReportResponse.AppointmentByDay> appointmentsByDay = byDayData.stream()
                .map(row -> {
                    // LocalDate from JPQL should be fine, but handle just in case
                    LocalDate date = row[0] instanceof java.sql.Date
                            ? ((java.sql.Date) row[0]).toLocalDate()
                            : (LocalDate) row[0];
                    return AppointmentReportResponse.AppointmentByDay.builder()
                            .date(date)
                            .appointmentCount(((Number) row[1]).intValue())
                            .completedCount(((Number) row[2]).intValue())
                            .build();
                })
                .collect(Collectors.toList());

        return AppointmentReportResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalAppointments(totalAppointments != null ? totalAppointments.intValue() : 0)
                .confirmedAppointments(confirmedAppointments != null ? confirmedAppointments.intValue() : 0)
                .completedAppointments(completedAppointments != null ? completedAppointments.intValue() : 0)
                .cancelledAppointments(cancelledAppointments != null ? cancelledAppointments.intValue() : 0)
                .noShowAppointments(0)
                .appointmentsByDoctor(appointmentsByDoctor)
                .appointmentsByDepartment(appointmentsByDepartment)
                .appointmentsByDay(appointmentsByDay)
                .build();
    }

    @Override
    public PatientReportResponse getPatientReport(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        // Bệnh nhân mới
        Long newPatients = patientRepository.countNewPatients(fromDateTime, toDateTime);

        // Bệnh nhân tái khám
        Long returningPatients = patientRepository.countReturningPatients(fromDate, toDate);

        // Bệnh nhân theo ngày
        List<Object[]> byDayData = patientRepository.getNewPatientsByDay(fromDateTime, toDateTime);
        List<PatientReportResponse.PatientByDay> patientsByDay = byDayData.stream()
                .map(row -> {
                    // Convert java.sql.Date to LocalDate
                    LocalDate date = row[0] instanceof java.sql.Date
                            ? ((java.sql.Date) row[0]).toLocalDate()
                            : (LocalDate) row[0];
                    return PatientReportResponse.PatientByDay.builder()
                            .date(date)
                            .newPatientCount(((Number) row[1]).intValue())
                            .returningPatientCount(0)
                            .build();
                })
                .collect(Collectors.toList());

        // Bệnh nhân theo giới tính
        List<Object[]> byGenderData = patientRepository.getPatientsByGender(fromDateTime, toDateTime);
        int totalForPercentage = newPatients.intValue();
        List<PatientReportResponse.PatientByGender> patientsByGender = byGenderData.stream()
                .map(row -> {
                    int count = ((Number) row[1]).intValue();
                    double percentage = totalForPercentage > 0 ? (count * 100.0 / totalForPercentage) : 0;
                    return PatientReportResponse.PatientByGender.builder()
                            .gender(row[0] != null ? row[0].toString() : "N/A")
                            .count(count)
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());

        // Bệnh nhân theo nhóm tuổi
        List<Object[]> byAgeData = patientRepository.getPatientsByAgeGroup(fromDateTime, toDateTime, LocalDate.now());
        List<PatientReportResponse.PatientByAgeGroup> patientsByAgeGroup = byAgeData.stream()
                .map(row -> {
                    int count = ((Number) row[1]).intValue();
                    double percentage = totalForPercentage > 0 ? (count * 100.0 / totalForPercentage) : 0;
                    return PatientReportResponse.PatientByAgeGroup.builder()
                            .ageGroup((String) row[0])
                            .count(count)
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());

        return PatientReportResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalNewPatients(newPatients.intValue())
                .totalReturningPatients(returningPatients.intValue())
                .totalPatients(newPatients.intValue() + returningPatients.intValue())
                .patientsByDay(patientsByDay)
                .patientsByGender(patientsByGender)
                .patientsByAgeGroup(patientsByAgeGroup)
                .build();
    }

    @Override
    public DoctorPerformanceResponse getDoctorPerformance(LocalDate fromDate, LocalDate toDate, Integer doctorId) {
        List<Object[]> performanceData = appointmentRepository.getAppointmentsByDoctor(fromDate, toDate);

        List<DoctorPerformanceResponse.DoctorPerformance> doctorPerformances = performanceData.stream()
                .filter(row -> doctorId == null || ((Number) row[0]).intValue() == doctorId)
                .map(row -> {
                    int total = ((Number) row[3]).intValue();
                    int completed = ((Number) row[4]).intValue();
                    int cancelled = ((Number) row[5]).intValue();
                    double completionRate = total > 0 ? (completed * 100.0 / total) : 0;

                    return DoctorPerformanceResponse.DoctorPerformance.builder()
                            .doctorId(((Number) row[0]).intValue())
                            .doctorName((String) row[1])
                            .departmentName((String) row[2])
                            .totalAppointments(total)
                            .completedAppointments(completed)
                            .cancelledAppointments(cancelled)
                            .totalPatients(total)
                            .totalRevenue(BigDecimal.ZERO)
                            .completionRate(Math.round(completionRate * 100.0) / 100.0)
                            .averageRating(0.0)
                            .totalRatings(0)
                            .build();
                })
                .collect(Collectors.toList());

        return DoctorPerformanceResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .doctorPerformances(doctorPerformances)
                .build();
    }

    @Override
    public ServiceReportResponse getServiceReport(LocalDate fromDate, LocalDate toDate) {
        // Dịch vụ phổ biến
        List<Object[]> popularData = examinationServiceRepository.getPopularServices(fromDate, toDate);
        List<ServiceReportResponse.PopularService> popularServices = popularData.stream()
                .map(row -> ServiceReportResponse.PopularService.builder()
                        .serviceId(((Number) row[0]).intValue())
                        .serviceName((String) row[1])
                        .usageCount(((Number) row[2]).intValue())
                        .totalRevenue((BigDecimal) row[3])
                        .price((BigDecimal) row[4])
                        .build())
                .collect(Collectors.toList());

        // Dịch vụ theo khoa
        List<Object[]> byDepartmentData = examinationServiceRepository.getServicesByDepartment(fromDate, toDate);
        List<ServiceReportResponse.ServiceByDepartment> servicesByDepartment = byDepartmentData.stream()
                .map(row -> ServiceReportResponse.ServiceByDepartment.builder()
                        .departmentId(((Number) row[0]).intValue())
                        .departmentName((String) row[1])
                        .serviceCount(((Number) row[2]).intValue())
                        .usageCount(((Number) row[3]).intValue())
                        .totalRevenue((BigDecimal) row[4])
                        .build())
                .collect(Collectors.toList());

        return ServiceReportResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalServices(popularServices.size())
                .popularServices(popularServices)
                .servicesByDepartment(servicesByDepartment)
                .build();
    }

    @Override
    public byte[] exportReportToPdf(String reportType, LocalDate fromDate, LocalDate toDate) {
        try {
            // Generate HTML and convert to PDF using existing PdfService
            Map<String, Object> params = new HashMap<>();
            params.put("fromDate", fromDate.toString());
            params.put("toDate", toDate.toString());
            params.put("reportType", reportType);
            return pdfService.render("reports/report", params, new ArrayList<>());
        } catch (Exception e) {
            log.error("Error exporting report to PDF", e);
            throw new RuntimeException("Failed to export report to PDF", e);
        }
    }

    @Override
    public byte[] exportReportToExcel(String reportType, LocalDate fromDate, LocalDate toDate) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Báo cáo");

            switch (reportType.toLowerCase()) {
                case "revenue":
                    exportRevenueToExcel(workbook, sheet, fromDate, toDate);
                    break;
                case "appointments":
                    exportAppointmentsToExcel(workbook, sheet, fromDate, toDate);
                    break;
                case "patients":
                    exportPatientsToExcel(workbook, sheet, fromDate, toDate);
                    break;
                case "doctor-performance":
                    exportDoctorPerformanceToExcel(workbook, sheet, fromDate, toDate);
                    break;
                case "services":
                    exportServicesToExcel(workbook, sheet, fromDate, toDate);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid report type: " + reportType);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting report to Excel", e);
            throw new RuntimeException("Failed to export report to Excel", e);
        }
    }

    // Helper methods for Excel export
    private void exportRevenueToExcel(Workbook workbook, Sheet sheet, LocalDate fromDate, LocalDate toDate) {
        RevenueReportResponse data = getRevenueReport(fromDate, toDate);

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = { "Ngày", "Doanh thu", "Số hóa đơn" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        // Data
        int rowNum = 1;
        for (RevenueReportResponse.RevenueByDay item : data.getRevenueByDays()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getDate().toString());
            row.createCell(1).setCellValue(item.getRevenue().doubleValue());
            row.createCell(2).setCellValue(item.getInvoiceCount());
        }

        // Summary
        Row summaryRow = sheet.createRow(rowNum + 1);
        summaryRow.createCell(0).setCellValue("TỔNG CỘNG");
        summaryRow.createCell(1).setCellValue(data.getTotalRevenue().doubleValue());
        summaryRow.createCell(2).setCellValue(data.getTotalInvoices());

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void exportAppointmentsToExcel(Workbook workbook, Sheet sheet, LocalDate fromDate, LocalDate toDate) {
        AppointmentReportResponse data = getAppointmentReport(fromDate, toDate, null, null);

        Row headerRow = sheet.createRow(0);
        String[] headers = { "Bác sĩ", "Khoa", "Tổng lịch", "Hoàn thành", "Hủy" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        int rowNum = 1;
        for (AppointmentReportResponse.AppointmentByDoctor item : data.getAppointmentsByDoctor()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getDoctorName());
            row.createCell(1).setCellValue(item.getDepartmentName());
            row.createCell(2).setCellValue(item.getTotalAppointments());
            row.createCell(3).setCellValue(item.getCompletedAppointments());
            row.createCell(4).setCellValue(item.getCancelledAppointments());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void exportPatientsToExcel(Workbook workbook, Sheet sheet, LocalDate fromDate, LocalDate toDate) {
        PatientReportResponse data = getPatientReport(fromDate, toDate);

        Row headerRow = sheet.createRow(0);
        String[] headers = { "Nhóm tuổi", "Số lượng", "Tỷ lệ %" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        int rowNum = 1;
        for (PatientReportResponse.PatientByAgeGroup item : data.getPatientsByAgeGroup()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getAgeGroup());
            row.createCell(1).setCellValue(item.getCount());
            row.createCell(2).setCellValue(item.getPercentage());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void exportDoctorPerformanceToExcel(Workbook workbook, Sheet sheet, LocalDate fromDate, LocalDate toDate) {
        DoctorPerformanceResponse data = getDoctorPerformance(fromDate, toDate, null);

        Row headerRow = sheet.createRow(0);
        String[] headers = { "Bác sĩ", "Khoa", "Tổng lịch", "Hoàn thành", "Tỷ lệ %" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        int rowNum = 1;
        for (DoctorPerformanceResponse.DoctorPerformance item : data.getDoctorPerformances()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getDoctorName());
            row.createCell(1).setCellValue(item.getDepartmentName());
            row.createCell(2).setCellValue(item.getTotalAppointments());
            row.createCell(3).setCellValue(item.getCompletedAppointments());
            row.createCell(4).setCellValue(item.getCompletionRate());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void exportServicesToExcel(Workbook workbook, Sheet sheet, LocalDate fromDate, LocalDate toDate) {
        ServiceReportResponse data = getServiceReport(fromDate, toDate);

        Row headerRow = sheet.createRow(0);
        String[] headers = { "Dịch vụ", "Số lần sử dụng", "Doanh thu", "Đơn giá" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        int rowNum = 1;
        for (ServiceReportResponse.PopularService item : data.getPopularServices()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getServiceName());
            row.createCell(1).setCellValue(item.getUsageCount());
            row.createCell(2).setCellValue(item.getTotalRevenue().doubleValue());
            row.createCell(3).setCellValue(item.getPrice().doubleValue());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private String generateHtmlReport(String reportType, LocalDate fromDate, LocalDate toDate) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("h1 { color: #333; text-align: center; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #4CAF50; color: white; }");
        html.append("tr:nth-child(even) { background-color: #f2f2f2; }");
        html.append(".summary { margin-top: 20px; font-weight: bold; }");
        html.append("</style></head><body>");

        switch (reportType.toLowerCase()) {
            case "revenue":
                html.append(generateRevenueHtml(fromDate, toDate));
                break;
            case "appointments":
                html.append(generateAppointmentsHtml(fromDate, toDate));
                break;
            case "patients":
                html.append(generatePatientsHtml(fromDate, toDate));
                break;
            case "doctor-performance":
                html.append(generateDoctorPerformanceHtml(fromDate, toDate));
                break;
            case "services":
                html.append(generateServicesHtml(fromDate, toDate));
                break;
        }

        html.append("</body></html>");
        return html.toString();
    }

    private String generateRevenueHtml(LocalDate fromDate, LocalDate toDate) {
        RevenueReportResponse data = getRevenueReport(fromDate, toDate);
        StringBuilder html = new StringBuilder();
        html.append("<h1>BÁO CÁO DOANH THU</h1>");
        html.append("<p>Từ ngày: ").append(fromDate).append(" - Đến ngày: ").append(toDate).append("</p>");
        html.append("<table>");
        html.append("<tr><th>Ngày</th><th>Doanh thu (VNĐ)</th><th>Số hóa đơn</th></tr>");
        for (RevenueReportResponse.RevenueByDay item : data.getRevenueByDays()) {
            html.append("<tr>");
            html.append("<td>").append(item.getDate()).append("</td>");
            html.append("<td>").append(String.format("%,.0f", item.getRevenue())).append("</td>");
            html.append("<td>").append(item.getInvoiceCount()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<div class='summary'>Tổng doanh thu: ").append(String.format("%,.0f", data.getTotalRevenue()))
                .append(" VNĐ</div>");
        return html.toString();
    }

    private String generateAppointmentsHtml(LocalDate fromDate, LocalDate toDate) {
        AppointmentReportResponse data = getAppointmentReport(fromDate, toDate, null, null);
        StringBuilder html = new StringBuilder();
        html.append("<h1>BÁO CÁO LỊCH KHÁM</h1>");
        html.append("<p>Từ ngày: ").append(fromDate).append(" - Đến ngày: ").append(toDate).append("</p>");
        html.append("<table>");
        html.append("<tr><th>Bác sĩ</th><th>Khoa</th><th>Tổng lịch</th><th>Hoàn thành</th><th>Hủy</th></tr>");
        for (AppointmentReportResponse.AppointmentByDoctor item : data.getAppointmentsByDoctor()) {
            html.append("<tr>");
            html.append("<td>").append(item.getDoctorName()).append("</td>");
            html.append("<td>").append(item.getDepartmentName()).append("</td>");
            html.append("<td>").append(item.getTotalAppointments()).append("</td>");
            html.append("<td>").append(item.getCompletedAppointments()).append("</td>");
            html.append("<td>").append(item.getCancelledAppointments()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    private String generatePatientsHtml(LocalDate fromDate, LocalDate toDate) {
        PatientReportResponse data = getPatientReport(fromDate, toDate);
        StringBuilder html = new StringBuilder();
        html.append("<h1>BÁO CÁO BỆNH NHÂN</h1>");
        html.append("<p>Từ ngày: ").append(fromDate).append(" - Đến ngày: ").append(toDate).append("</p>");
        html.append("<div class='summary'>Bệnh nhân mới: ").append(data.getTotalNewPatients()).append("</div>");
        html.append("<div class='summary'>Bệnh nhân tái khám: ").append(data.getTotalReturningPatients())
                .append("</div>");
        html.append("<table>");
        html.append("<tr><th>Nhóm tuổi</th><th>Số lượng</th><th>Tỷ lệ %</th></tr>");
        for (PatientReportResponse.PatientByAgeGroup item : data.getPatientsByAgeGroup()) {
            html.append("<tr>");
            html.append("<td>").append(item.getAgeGroup()).append("</td>");
            html.append("<td>").append(item.getCount()).append("</td>");
            html.append("<td>").append(item.getPercentage()).append("%</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    private String generateDoctorPerformanceHtml(LocalDate fromDate, LocalDate toDate) {
        DoctorPerformanceResponse data = getDoctorPerformance(fromDate, toDate, null);
        StringBuilder html = new StringBuilder();
        html.append("<h1>BÁO CÁO HIỆU SUẤT BÁC SĨ</h1>");
        html.append("<p>Từ ngày: ").append(fromDate).append(" - Đến ngày: ").append(toDate).append("</p>");
        html.append("<table>");
        html.append("<tr><th>Bác sĩ</th><th>Khoa</th><th>Tổng lịch</th><th>Hoàn thành</th><th>Tỷ lệ %</th></tr>");
        for (DoctorPerformanceResponse.DoctorPerformance item : data.getDoctorPerformances()) {
            html.append("<tr>");
            html.append("<td>").append(item.getDoctorName()).append("</td>");
            html.append("<td>").append(item.getDepartmentName()).append("</td>");
            html.append("<td>").append(item.getTotalAppointments()).append("</td>");
            html.append("<td>").append(item.getCompletedAppointments()).append("</td>");
            html.append("<td>").append(item.getCompletionRate()).append("%</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    private String generateServicesHtml(LocalDate fromDate, LocalDate toDate) {
        ServiceReportResponse data = getServiceReport(fromDate, toDate);
        StringBuilder html = new StringBuilder();
        html.append("<h1>BÁO CÁO DỊCH VỤ PHỔ BIẾN</h1>");
        html.append("<p>Từ ngày: ").append(fromDate).append(" - Đến ngày: ").append(toDate).append("</p>");
        html.append("<table>");
        html.append("<tr><th>Dịch vụ</th><th>Số lần sử dụng</th><th>Doanh thu (VNĐ)</th><th>Đơn giá</th></tr>");
        for (ServiceReportResponse.PopularService item : data.getPopularServices()) {
            html.append("<tr>");
            html.append("<td>").append(item.getServiceName()).append("</td>");
            html.append("<td>").append(item.getUsageCount()).append("</td>");
            html.append("<td>").append(String.format("%,.0f", item.getTotalRevenue())).append("</td>");
            html.append("<td>").append(String.format("%,.0f", item.getPrice())).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }
}
