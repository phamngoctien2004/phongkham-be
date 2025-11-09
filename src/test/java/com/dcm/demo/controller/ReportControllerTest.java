package com.dcm.demo.controller;

import com.dcm.demo.dto.response.*;
import com.dcm.demo.service.interfaces.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRevenueReport_ShouldReturnOk() throws Exception {
        // Given
        RevenueReportResponse response = RevenueReportResponse.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .totalRevenue(new BigDecimal("150000000"))
                .totalInvoices(245)
                .revenueByDays(new ArrayList<>())
                .revenueByPaymentMethods(new ArrayList<>())
                .build();

        when(reportService.getRevenueReport(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/reports/revenue")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get revenue report successfully"))
                .andExpect(jsonPath("$.data.totalRevenue").value(150000000))
                .andExpect(jsonPath("$.data.totalInvoices").value(245));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAppointmentReport_ShouldReturnOk() throws Exception {
        // Given
        AppointmentReportResponse response = AppointmentReportResponse.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .totalAppointments(450)
                .completedAppointments(380)
                .appointmentsByDoctor(new ArrayList<>())
                .appointmentsByDepartment(new ArrayList<>())
                .appointmentsByDay(new ArrayList<>())
                .build();

        when(reportService.getAppointmentReport(any(LocalDate.class), any(LocalDate.class), eq(null), eq(null)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/reports/appointments")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalAppointments").value(450))
                .andExpect(jsonPath("$.data.completedAppointments").value(380));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAppointmentReport_WithDoctorId_ShouldReturnOk() throws Exception {
        // Given
        AppointmentReportResponse response = AppointmentReportResponse.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .totalAppointments(120)
                .appointmentsByDoctor(new ArrayList<>())
                .appointmentsByDepartment(new ArrayList<>())
                .appointmentsByDay(new ArrayList<>())
                .build();

        when(reportService.getAppointmentReport(any(LocalDate.class), any(LocalDate.class), eq(1), eq(null)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/reports/appointments")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31")
                .param("doctorId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAppointments").value(120));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPatientReport_ShouldReturnOk() throws Exception {
        // Given
        PatientReportResponse response = PatientReportResponse.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .totalNewPatients(320)
                .totalReturningPatients(580)
                .totalPatients(900)
                .patientsByDay(new ArrayList<>())
                .patientsByGender(new ArrayList<>())
                .patientsByAgeGroup(new ArrayList<>())
                .build();

        when(reportService.getPatientReport(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/reports/patients")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalNewPatients").value(320))
                .andExpect(jsonPath("$.data.totalReturningPatients").value(580))
                .andExpect(jsonPath("$.data.totalPatients").value(900));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDoctorPerformance_ShouldReturnOk() throws Exception {
        // Given
        List<DoctorPerformanceResponse.DoctorPerformance> performances = new ArrayList<>();
        performances.add(DoctorPerformanceResponse.DoctorPerformance.builder()
                .doctorId(1)
                .doctorName("BS. Nguyễn Văn A")
                .departmentName("Khoa Tim Mạch")
                .totalAppointments(120)
                .completedAppointments(110)
                .completionRate(91.67)
                .build());

        DoctorPerformanceResponse response = DoctorPerformanceResponse.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .doctorPerformances(performances)
                .build();

        when(reportService.getDoctorPerformance(any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/reports/doctor-performance")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.doctorPerformances[0].doctorName").value("BS. Nguyễn Văn A"))
                .andExpect(jsonPath("$.data.doctorPerformances[0].completionRate").value(91.67));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getServiceReport_ShouldReturnOk() throws Exception {
        // Given
        List<ServiceReportResponse.PopularService> services = new ArrayList<>();
        services.add(ServiceReportResponse.PopularService.builder()
                .serviceId(1)
                .serviceName("Khám tim mạch")
                .usageCount(250)
                .totalRevenue(new BigDecimal("62500000"))
                .build());

        ServiceReportResponse response = ServiceReportResponse.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .totalServices(1)
                .popularServices(services)
                .servicesByDepartment(new ArrayList<>())
                .build();

        when(reportService.getServiceReport(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/reports/services")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalServices").value(1))
                .andExpect(jsonPath("$.data.popularServices[0].serviceName").value("Khám tim mạch"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void exportReportToPdf_ShouldReturnPdf() throws Exception {
        // Given
        byte[] pdfData = new byte[] { 1, 2, 3, 4, 5 };
        when(reportService.exportReportToPdf(eq("revenue"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(pdfData);

        // When & Then
        mockMvc.perform(get("/api/reports/export/pdf")
                .param("reportType", "revenue")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=report.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void exportReportToExcel_ShouldReturnExcel() throws Exception {
        // Given
        byte[] excelData = new byte[] { 1, 2, 3, 4, 5 };
        when(reportService.exportReportToExcel(eq("revenue"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(excelData);

        // When & Then
        mockMvc.perform(get("/api/reports/export/excel")
                .param("reportType", "revenue")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=report.xlsx"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardReport_ShouldReturnAllReports() throws Exception {
        // Given
        RevenueReportResponse revenueData = RevenueReportResponse.builder()
                .totalRevenue(new BigDecimal("150000000"))
                .build();

        AppointmentReportResponse appointmentsData = AppointmentReportResponse.builder()
                .totalAppointments(450)
                .build();

        PatientReportResponse patientsData = PatientReportResponse.builder()
                .totalNewPatients(320)
                .build();

        ServiceReportResponse servicesData = ServiceReportResponse.builder()
                .totalServices(15)
                .build();

        when(reportService.getRevenueReport(any(), any())).thenReturn(revenueData);
        when(reportService.getAppointmentReport(any(), any(), eq(null), eq(null))).thenReturn(appointmentsData);
        when(reportService.getPatientReport(any(), any())).thenReturn(patientsData);
        when(reportService.getServiceReport(any(), any())).thenReturn(servicesData);

        // When & Then
        mockMvc.perform(get("/api/reports/dashboard")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.revenue").exists())
                .andExpect(jsonPath("$.data.appointments").exists())
                .andExpect(jsonPath("$.data.patients").exists())
                .andExpect(jsonPath("$.data.services").exists());
    }

    @Test
    void getRevenueReport_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reports/revenue")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRevenueReport_WithInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reports/revenue")
                .param("fromDate", "invalid-date")
                .param("toDate", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRevenueReport_WithMissingParams_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reports/revenue")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
