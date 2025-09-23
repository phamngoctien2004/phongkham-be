package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.response.MedicalResponse;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.MedicalMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.MedicalRecordRepository;
import com.dcm.demo.repository.RelationshipRepository;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordRepository repository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final UserService userService;
    private final RelationshipRepository relationshipRepository;
    private final HealthPlanService healthPlanService;
    private final MedicalMapper mapper;
    @Override
    @Transactional
    public void create(MedicalRequest request) {
        MedicalRecord medicalRecord = new MedicalRecord();

        Patient patient = new Patient();
        patient.setId(request.getPatientId());
        medicalRecord.setPatient(patient);

        medicalRecord.setCode("PK" + System.currentTimeMillis());
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setClinicalExamination(request.getClinicalExamination());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTreatmentPlan(request.getTreatmentPlan());
        medicalRecord.setNote(request.getNote());
        BigDecimal fee = BigDecimal.ZERO;
        if(request.getDoctorId() != null) {
            Doctor doctor = doctorService.findById(request.getDoctorId());
            medicalRecord.setDoctor(doctor);
            fee = fee.add(doctor.getDegree().getExaminationFee());
            medicalRecord.setFee(fee);
            medicalRecord.setTotal(fee);
        }

        if (request.getHealthPlanId() != null) {
            HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
            medicalRecord.setHealthPlan(healthPlan);
            medicalRecord.setTotal(fee.add(healthPlan.getPrice()));
        }

        repository.save(medicalRecord);
    }

    @Override
    public void update(MedicalRequest request) {
        MedicalRecord medicalRecord = repository.findById(request.getId()).orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOTFOUND)
        );

        Doctor doctor = new Doctor();
        doctor.setId(request.getDoctorId());
        medicalRecord.setDoctor(doctor);
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setClinicalExamination(request.getClinicalExamination());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTreatmentPlan(request.getTreatmentPlan());
        medicalRecord.setNote(request.getNote());
        repository.save(medicalRecord);
    }

    @Override
    public List<MedicalResponse> getRelationMedicalRecord(String cccd) {
        User user = userService.getCurrentUser();
        Patient patient = patientService.findByCccd(cccd);
//        check relationship
        relationshipRepository.findByPatientIdAndUserId(patient.getId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Not found relationship"));

        return repository.findByPatientIdOrderByDateDesc(patient.getId()).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicalResponse> me() {
        PatientResponse currentPatient = patientService.me();
        return repository.findByPatientIdOrderByDateDesc(currentPatient.getId()).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public MedicalRecord findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Medical record not found"));
    }

}
