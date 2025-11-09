package com.dcm.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentReportResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer totalAppointments;
    private Integer confirmedAppointments;
    private Integer completedAppointments;
    private Integer cancelledAppointments;
    private Integer noShowAppointments;
    private List<AppointmentByDoctor> appointmentsByDoctor;
    private List<AppointmentByDepartment> appointmentsByDepartment;
    private List<AppointmentByDay> appointmentsByDay;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppointmentByDoctor {
        private Integer doctorId;
        private String doctorName;
        private String departmentName;
        private Integer totalAppointments;
        private Integer completedAppointments;
        private Integer cancelledAppointments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppointmentByDepartment {
        private Integer departmentId;
        private String departmentName;
        private Integer totalAppointments;
        private Integer completedAppointments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppointmentByDay {
        private LocalDate date;
        private Integer appointmentCount;
        private Integer completedCount;
    }
}

