package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.request.VerifyOtpRequest;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.dto.response.PatientsDto;
import com.dcm.demo.model.Patient;
import com.dcm.demo.model.Relationship;
import com.dcm.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    PatientResponse create(PatientRequest request);
    PatientResponse update(PatientRequest request);
    PatientResponse createPatientAndAccount(PatientRequest request);
    PatientResponse createPatient(PatientRequest request, User user);
    void delete(Integer id);
    Patient findByPhone(String phone);
    Patient findByCccd(String cccd);
    PatientResponse findById(Integer id);
    Patient findEntityById(Integer id);
    List<PatientResponse> findAll(String keyword);
    Page<PatientResponse> findAll(String keyword, Pageable pageable);

    PatientResponse me();
    Patient save(Patient patient);
    List<PatientResponse> all();
    Relationship buildRelationship(Patient patient, User user, String relational);
    void addRelationship(PatientRequest request);
    void syncRelationship(VerifyOtpRequest request);
    void deleteRelationship(Integer patientId);
}
