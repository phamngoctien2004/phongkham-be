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
public class PatientReportResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer totalNewPatients;
    private Integer totalReturningPatients;
    private Integer totalPatients;
    private List<PatientByDay> patientsByDay;
    private List<PatientByGender> patientsByGender;
    private List<PatientByAgeGroup> patientsByAgeGroup;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PatientByDay {
        private LocalDate date;
        private Integer newPatientCount;
        private Integer returningPatientCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PatientByGender {
        private String gender;
        private Integer count;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PatientByAgeGroup {
        private String ageGroup;
        private Integer count;
        private Double percentage;
    }
}

