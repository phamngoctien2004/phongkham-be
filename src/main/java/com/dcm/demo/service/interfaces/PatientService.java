package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.model.User;

import java.util.List;

public interface PatientService {
    PatientResponse create(PatientRequest request);
    PatientResponse createPatientAndAccount(PatientRequest request);
    PatientResponse createPatient(PatientRequest request, User user);

    List<PatientResponse> findAllPatientByPhone(String phone);
}
