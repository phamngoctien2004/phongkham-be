package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.dto.response.PatientsDto;
import com.dcm.demo.model.Patient;
import com.dcm.demo.model.User;

import java.util.List;

public interface PatientService {
    PatientResponse create(PatientRequest request);
    PatientResponse update(PatientRequest request);
    PatientResponse createPatientAndAccount(PatientRequest request);
    PatientResponse createPatient(PatientRequest request, User user);
    Patient findByPhone(String phone);
    Patient findByCccd(String cccd);
    PatientResponse findById(Integer id);
    List<PatientResponse> findAll(String keyword);

    PatientResponse me();
    List<PatientResponse> all();
}
