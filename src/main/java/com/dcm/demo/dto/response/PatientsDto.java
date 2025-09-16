package com.dcm.demo.dto.response;

import com.dcm.demo.model.Patient;
import lombok.Data;

import java.util.List;

@Data
public class PatientsDto {
    private List<PatientResponse> patients;
    private Integer ownerId;
}
