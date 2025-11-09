package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.*;
import com.dcm.demo.model.Appointment;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ExaminationServiceRepository examinationServiceRepository;

    @Mock
    private PdfService pdfService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;

    @BeforeEach
    void setUp() {
        fromDate = LocalDate.of(2024, 1, 1);
        toDate = LocalDate.of(2024, 12, 31);
        fromDateTime = fromDate.atStartOfDay();
        toDateTime = toDate.atTime(LocalTime.MAX);
    }

    // ==================== REVENUE REPORT TESTS ====================

    @Test
    void getRevenueReport_ShouldReturnCorrectData() {
        // Given
        BigDecimal totalRevenue = new BigDecimal("150000000");
        Long paidInvoices = 245L;
        Long unpaidInvoices = 10L;

        List<Object[]> revenueByDayData = new ArrayList<>();
        revenueByDayData.add(new Object[] { LocalDate.of(2024, 1, 1), new BigDecimal("5000000"), 10 });
        revenueByDayData.add(new Object[] { LocalDate.of(2024, 1, 2), new BigDecimal("7500000"), 15 });

        List<Object[]> revenueByMethodData = new ArrayList<>();
        revenueByMethodData.add(new Object[] { "TIEN_MAT", new BigDecimal("80000000"), 150 });
        revenueByMethodData.add(new Object[] { "CHUYEN_KHOAN", new BigDecimal("70000000"), 95 });

        when(invoiceRepository.getTotalRevenue(fromDateTime, toDateTime)).thenReturn(totalRevenue);
        when(invoiceRepository.countByStatusAndDateRange(fromDateTime, toDateTime, Invoice.PaymentStatus.DA_THANH_TOAN))
                .thenReturn(paidInvoices);
        when(invoiceRepository.countByStatusAndDateRange(fromDateTime, toDateTime,
                Invoice.PaymentStatus.CHUA_THANH_TOAN))
                .thenReturn(unpaidInvoices);
        when(invoiceRepository.getRevenueByDay(fromDateTime, toDateTime)).thenReturn(revenueByDayData);
        when(invoiceRepository.getRevenueByPaymentMethod(fromDateTime, toDateTime)).thenReturn(revenueByMethodData);

        // When
        RevenueReportResponse result = reportService.getRevenueReport(fromDate, toDate);

        // Then
        assertNotNull(result);
        assertEquals(totalRevenue, result.getTotalRevenue());
        assertEquals(255, result.getTotalInvoices());
        assertEquals(245, result.getTotalPaidInvoices());
        assertEquals(10, result.getTotalUnpaidInvoices());
        assertEquals(2, result.getRevenueByDays().size());
        assertEquals(2, result.getRevenueByPaymentMethods().size());

        // Verify interactions
        verify(invoiceRepository).getTotalRevenue(fromDateTime, toDateTime);
        verify(invoiceRepository, times(2)).countByStatusAndDateRange(any(), any(), any());
    }

    @Test
    void getRevenueReport_WhenNoData_ShouldReturnZeroRevenue() {
        // Given
        when(invoiceRepository.getTotalRevenue(fromDateTime, toDateTime)).thenReturn(null);
        when(invoiceRepository.countByStatusAndDateRange(any(), any(), any())).thenReturn(0L);
        when(invoiceRepository.getRevenueByDay(fromDateTime, toDateTime)).thenReturn(new ArrayList<>());
        when(invoiceRepository.getRevenueByPaymentMethod(fromDateTime, toDateTime)).thenReturn(new ArrayList<>());

        // When
        RevenueReportResponse result = reportService.getRevenueReport(fromDate, toDate);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(0, result.getTotalInvoices());
        assertTrue(result.getRevenueByDays().isEmpty());
        assertTrue(result.getRevenueByPaymentMethods().isEmpty());
    }

    // ==================== APPOINTMENT REPORT TESTS ====================

    @Test
    void getAppointmentReport_ShouldReturnCorrectData() {
        // Given
        Long totalAppointments = 450L;
        Long confirmedAppointments = 420L;
        Long completedAppointments = 380L;
        Long cancelledAppointments = 40L;

        List<Object[]> byDoctorData = new ArrayList<>();
        byDoctorData.add(new Object[] { 1, "BS. Nguyễn Văn A", "Khoa Tim Mạch", 120, 110, 10 });

        List<Object[]> byDepartmentData = new ArrayList<>();
        byDepartmentData.add(new Object[] { 2, "Khoa Tim Mạch", 200, 180 });

        List<Object[]> byDayData = new ArrayList<>();
        byDayData.add(new Object[] { LocalDate.of(2024, 1, 1), 15, 14 });

        when(appointmentRepository.countByDateRangeAndStatus(fromDate, toDate, null)).thenReturn(totalAppointments);
        when(appointmentRepository.countByDateRangeAndStatus(fromDate, toDate,
                Appointment.AppointmentStatus.DA_XAC_NHAN))
                .thenReturn(confirmedAppointments);
        when(appointmentRepository.countByDateRangeAndStatus(fromDate, toDate,
                Appointment.AppointmentStatus.HOAN_THANH))
                .thenReturn(completedAppointments);
        when(appointmentRepository.countByDateRangeAndStatus(fromDate, toDate, Appointment.AppointmentStatus.HUY))
                .thenReturn(cancelledAppointments);
        when(appointmentRepository.getAppointmentsByDoctor(fromDate, toDate)).thenReturn(byDoctorData);
        when(appointmentRepository.getAppointmentsByDepartment(fromDate, toDate)).thenReturn(byDepartmentData);
        when(appointmentRepository.getAppointmentsByDay(fromDate, toDate)).thenReturn(byDayData);

        // When
        AppointmentReportResponse result = reportService.getAppointmentReport(fromDate, toDate, null, null);

        // Then
        assertNotNull(result);
        assertEquals(450, result.getTotalAppointments());
        assertEquals(420, result.getConfirmedAppointments());
        assertEquals(380, result.getCompletedAppointments());
        assertEquals(40, result.getCancelledAppointments());
        assertEquals(1, result.getAppointmentsByDoctor().size());
        assertEquals(1, result.getAppointmentsByDepartment().size());
        assertEquals(1, result.getAppointmentsByDay().size());

        // Verify doctor data
        AppointmentReportResponse.AppointmentByDoctor doctorData = result.getAppointmentsByDoctor().get(0);
        assertEquals("BS. Nguyễn Văn A", doctorData.getDoctorName());
        assertEquals("Khoa Tim Mạch", doctorData.getDepartmentName());
        assertEquals(120, doctorData.getTotalAppointments());
    }

    @Test
    void getAppointmentReport_WithDoctorFilter_ShouldFilterCorrectly() {
        // Given
        Integer doctorId = 1;
        List<Object[]> byDoctorData = new ArrayList<>();
        byDoctorData.add(new Object[] { 1, "BS. Nguyễn Văn A", "Khoa Tim Mạch", 120, 110, 10 });
        byDoctorData.add(new Object[] { 2, "BS. Trần Thị B", "Khoa Nội", 95, 92, 3 });

        when(appointmentRepository.countByDateRangeAndStatus(any(), any(), any())).thenReturn(0L);
        when(appointmentRepository.getAppointmentsByDoctor(fromDate, toDate)).thenReturn(byDoctorData);
        when(appointmentRepository.getAppointmentsByDepartment(fromDate, toDate)).thenReturn(new ArrayList<>());
        when(appointmentRepository.getAppointmentsByDay(fromDate, toDate)).thenReturn(new ArrayList<>());

        // When
        AppointmentReportResponse result = reportService.getAppointmentReport(fromDate, toDate, doctorId, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getAppointmentsByDoctor().size());
        assertEquals(1, result.getAppointmentsByDoctor().get(0).getDoctorId());
    }

    // ==================== PATIENT REPORT TESTS ====================

    @Test
    void getPatientReport_ShouldReturnCorrectData() {
        // Given
        Long newPatients = 320L;
        Long returningPatients = 580L;

        List<Object[]> byDayData = new ArrayList<>();
        byDayData.add(new Object[] { LocalDate.of(2024, 1, 1), 8 });

        List<Object[]> byGenderData = new ArrayList<>();
        byGenderData.add(new Object[] { "NAM", 180 });
        byGenderData.add(new Object[] { "NU", 140 });

        List<Object[]> byAgeData = new ArrayList<>();
        byAgeData.add(new Object[] { "18-30", 120 });
        byAgeData.add(new Object[] { "31-50", 95 });

        when(patientRepository.countNewPatients(fromDateTime, toDateTime)).thenReturn(newPatients);
        when(patientRepository.countReturningPatients(fromDate, toDate)).thenReturn(returningPatients);
        when(patientRepository.getNewPatientsByDay(fromDateTime, toDateTime)).thenReturn(byDayData);
        when(patientRepository.getPatientsByGender(fromDateTime, toDateTime)).thenReturn(byGenderData);
        when(patientRepository.getPatientsByAgeGroup(eq(fromDateTime), eq(toDateTime), any())).thenReturn(byAgeData);

        // When
        PatientReportResponse result = reportService.getPatientReport(fromDate, toDate);

        // Then
        assertNotNull(result);
        assertEquals(320, result.getTotalNewPatients());
        assertEquals(580, result.getTotalReturningPatients());
        assertEquals(900, result.getTotalPatients());
        assertEquals(2, result.getPatientsByGender().size());
        assertEquals(2, result.getPatientsByAgeGroup().size());

        // Verify percentages
        PatientReportResponse.PatientByGender genderData = result.getPatientsByGender().get(0);
        assertEquals("NAM", genderData.getGender());
        assertEquals(180, genderData.getCount());
        assertEquals(56.25, genderData.getPercentage(), 0.01);
    }

    @Test
    void getPatientReport_WithZeroPatients_ShouldHandleGracefully() {
        // Given
        when(patientRepository.countNewPatients(fromDateTime, toDateTime)).thenReturn(0L);
        when(patientRepository.countReturningPatients(fromDate, toDate)).thenReturn(0L);
        when(patientRepository.getNewPatientsByDay(fromDateTime, toDateTime)).thenReturn(new ArrayList<>());
        when(patientRepository.getPatientsByGender(fromDateTime, toDateTime)).thenReturn(new ArrayList<>());
        when(patientRepository.getPatientsByAgeGroup(any(), any(), any())).thenReturn(new ArrayList<>());

        // When
        PatientReportResponse result = reportService.getPatientReport(fromDate, toDate);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalNewPatients());
        assertEquals(0, result.getTotalReturningPatients());
        assertEquals(0, result.getTotalPatients());
    }

    // ==================== DOCTOR PERFORMANCE TESTS ====================

    @Test
    void getDoctorPerformance_ShouldCalculateCompletionRate() {
        // Given
        List<Object[]> performanceData = new ArrayList<>();
        performanceData.add(new Object[] { 1, "BS. Nguyễn Văn A", "Khoa Tim Mạch", 120, 110, 10 });
        performanceData.add(new Object[] { 2, "BS. Trần Thị B", "Khoa Nội", 100, 95, 5 });

        when(appointmentRepository.getAppointmentsByDoctor(fromDate, toDate)).thenReturn(performanceData);

        // When
        DoctorPerformanceResponse result = reportService.getDoctorPerformance(fromDate, toDate, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getDoctorPerformances().size());

        DoctorPerformanceResponse.DoctorPerformance doctor1 = result.getDoctorPerformances().get(0);
        assertEquals("BS. Nguyễn Văn A", doctor1.getDoctorName());
        assertEquals(120, doctor1.getTotalAppointments());
        assertEquals(110, doctor1.getCompletedAppointments());
        assertEquals(91.67, doctor1.getCompletionRate(), 0.01);

        DoctorPerformanceResponse.DoctorPerformance doctor2 = result.getDoctorPerformances().get(1);
        assertEquals(95.0, doctor2.getCompletionRate(), 0.01);
    }

    @Test
    void getDoctorPerformance_WithZeroAppointments_ShouldReturnZeroRate() {
        // Given
        List<Object[]> performanceData = new ArrayList<>();
        performanceData.add(new Object[] { 1, "BS. Nguyễn Văn A", "Khoa Tim Mạch", 0, 0, 0 });

        when(appointmentRepository.getAppointmentsByDoctor(fromDate, toDate)).thenReturn(performanceData);

        // When
        DoctorPerformanceResponse result = reportService.getDoctorPerformance(fromDate, toDate, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getDoctorPerformances().size());
        assertEquals(0.0, result.getDoctorPerformances().get(0).getCompletionRate());
    }

    // ==================== SERVICE REPORT TESTS ====================

    @Test
    void getServiceReport_ShouldReturnPopularServices() {
        // Given
        List<Object[]> popularData = new ArrayList<>();
        popularData.add(new Object[] { 1, "Khám tim mạch", 250, new BigDecimal("62500000"), new BigDecimal("250000") });
        popularData.add(new Object[] { 2, "Siêu âm tim", 180, new BigDecimal("90000000"), new BigDecimal("500000") });

        List<Object[]> byDepartmentData = new ArrayList<>();
        byDepartmentData.add(new Object[] { 1, "Khoa Tim Mạch", 5, 430, new BigDecimal("152500000") });

        when(examinationServiceRepository.getPopularServices(fromDate, toDate)).thenReturn(popularData);
        when(examinationServiceRepository.getServicesByDepartment(fromDate, toDate)).thenReturn(byDepartmentData);

        // When
        ServiceReportResponse result = reportService.getServiceReport(fromDate, toDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalServices());
        assertEquals(2, result.getPopularServices().size());
        assertEquals(1, result.getServicesByDepartment().size());

        ServiceReportResponse.PopularService service1 = result.getPopularServices().get(0);
        assertEquals("Khám tim mạch", service1.getServiceName());
        assertEquals(250, service1.getUsageCount());
        assertEquals(new BigDecimal("62500000"), service1.getTotalRevenue());
    }

    // ==================== EXPORT EXCEL TESTS ====================

    @Test
    void exportReportToExcel_WithRevenueType_ShouldReturnExcelBytes() {
        // Given
        BigDecimal totalRevenue = new BigDecimal("150000000");
        when(invoiceRepository.getTotalRevenue(any(), any())).thenReturn(totalRevenue);
        when(invoiceRepository.countByStatusAndDateRange(any(), any(), any())).thenReturn(10L);
        when(invoiceRepository.getRevenueByDay(any(), any())).thenReturn(new ArrayList<>());
        when(invoiceRepository.getRevenueByPaymentMethod(any(), any())).thenReturn(new ArrayList<>());

        // When
        byte[] result = reportService.exportReportToExcel("revenue", fromDate, toDate);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void exportReportToExcel_WithInvalidType_ShouldThrowException() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.exportReportToExcel("invalid-type", fromDate, toDate);
        });

        // Verify the cause is IllegalArgumentException
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertTrue(exception.getMessage().contains("Failed to export report to Excel"));
    }

    @Test
    void exportReportToExcel_WithAppointmentsType_ShouldReturnExcelBytes() {
        // Given
        when(appointmentRepository.countByDateRangeAndStatus(any(), any(), any())).thenReturn(10L);
        when(appointmentRepository.getAppointmentsByDoctor(any(), any())).thenReturn(new ArrayList<>());
        when(appointmentRepository.getAppointmentsByDepartment(any(), any())).thenReturn(new ArrayList<>());
        when(appointmentRepository.getAppointmentsByDay(any(), any())).thenReturn(new ArrayList<>());

        // When
        byte[] result = reportService.exportReportToExcel("appointments", fromDate, toDate);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void exportReportToExcel_WithPatientsType_ShouldReturnExcelBytes() {
        // Given
        when(patientRepository.countNewPatients(any(), any())).thenReturn(100L);
        when(patientRepository.countReturningPatients(any(), any())).thenReturn(50L);
        when(patientRepository.getNewPatientsByDay(any(), any())).thenReturn(new ArrayList<>());
        when(patientRepository.getPatientsByGender(any(), any())).thenReturn(new ArrayList<>());
        when(patientRepository.getPatientsByAgeGroup(any(), any(), any())).thenReturn(new ArrayList<>());

        // When
        byte[] result = reportService.exportReportToExcel("patients", fromDate, toDate);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    // ==================== EXPORT PDF TESTS ====================

    @Test
    void exportReportToPdf_ShouldCallPdfService() {
        // Given
        byte[] expectedPdf = new byte[] { 1, 2, 3 };
        when(pdfService.render(any(), any(), any())).thenReturn(expectedPdf);

        // When
        byte[] result = reportService.exportReportToPdf("revenue", fromDate, toDate);

        // Then
        assertNotNull(result);
        assertArrayEquals(expectedPdf, result);
        verify(pdfService).render(any(), any(), any());
    }

    // ==================== EDGE CASES TESTS ====================

    @Test
    void getRevenueReport_WithSameDateRange_ShouldWork() {
        // Given
        LocalDate sameDate = LocalDate.of(2024, 1, 1);
        when(invoiceRepository.getTotalRevenue(any(), any())).thenReturn(BigDecimal.ZERO);
        when(invoiceRepository.countByStatusAndDateRange(any(), any(), any())).thenReturn(0L);
        when(invoiceRepository.getRevenueByDay(any(), any())).thenReturn(new ArrayList<>());
        when(invoiceRepository.getRevenueByPaymentMethod(any(), any())).thenReturn(new ArrayList<>());

        // When
        RevenueReportResponse result = reportService.getRevenueReport(sameDate, sameDate);

        // Then
        assertNotNull(result);
        assertEquals(sameDate, result.getFromDate());
        assertEquals(sameDate, result.getToDate());
    }

    @Test
    void getPatientReport_PercentageCalculation_ShouldBeAccurate() {
        // Given
        Long newPatients = 100L;
        List<Object[]> byGenderData = new ArrayList<>();
        byGenderData.add(new Object[] { "NAM", 60 });
        byGenderData.add(new Object[] { "NU", 40 });

        when(patientRepository.countNewPatients(any(), any())).thenReturn(newPatients);
        when(patientRepository.countReturningPatients(any(), any())).thenReturn(0L);
        when(patientRepository.getNewPatientsByDay(any(), any())).thenReturn(new ArrayList<>());
        when(patientRepository.getPatientsByGender(any(), any())).thenReturn(byGenderData);
        when(patientRepository.getPatientsByAgeGroup(any(), any(), any())).thenReturn(new ArrayList<>());

        // When
        PatientReportResponse result = reportService.getPatientReport(fromDate, toDate);

        // Then
        assertEquals(60.0, result.getPatientsByGender().get(0).getPercentage());
        assertEquals(40.0, result.getPatientsByGender().get(1).getPercentage());
    }

    @Test
    void getAppointmentReport_WithNullCounts_ShouldHandleGracefully() {
        // Given
        when(appointmentRepository.countByDateRangeAndStatus(any(), any(), any())).thenReturn(null);
        when(appointmentRepository.getAppointmentsByDoctor(any(), any())).thenReturn(new ArrayList<>());
        when(appointmentRepository.getAppointmentsByDepartment(any(), any())).thenReturn(new ArrayList<>());
        when(appointmentRepository.getAppointmentsByDay(any(), any())).thenReturn(new ArrayList<>());

        // When
        AppointmentReportResponse result = reportService.getAppointmentReport(fromDate, toDate, null, null);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalAppointments());
        assertEquals(0, result.getConfirmedAppointments());
    }
}
