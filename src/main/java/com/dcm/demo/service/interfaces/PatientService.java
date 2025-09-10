package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.PatientCreateRequest;
import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.model.Patient;

public interface PatientService {
    PatientResponse createPatient(PatientRequest request);
}
