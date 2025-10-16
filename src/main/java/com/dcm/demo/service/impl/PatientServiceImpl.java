package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.OtpRequest;
import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.request.VerifyOtpRequest;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.mapper.PatientMapper;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.model.Patient;
import com.dcm.demo.model.Relationship;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.PatientRepository;
import com.dcm.demo.service.interfaces.PatientService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PatientMapper patientMapper;
    private final SendMessage sendMessage;

    @Override
    @Transactional
    public PatientResponse create(PatientRequest request) {
        return createPatientAndAccount(request);
    }

    @Override
    @Transactional
    public PatientResponse update(PatientRequest request) {
        Patient patient = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patient.setPhone(request.getPhone());
        patient.setFullName(request.getFullName());
        patient.setAddress(request.getAddress());
        patient.setGender(request.getGender());
        patient.setCccd(request.getCccd());
        patient.setBirth(request.getBirth());
        patient.setBloodType(request.getBloodType());
        patient.setWeight(request.getWeight());
        patient.setHeight(request.getHeight());

        if (patient.getPhone() != null) {
            User user = userService.findByPhone(patient.getPhone())
                    .orElse(null);
            if(user != null){
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                userService.save(user);
            }
        }
        if(request.getPhoneLink() != null){

        }
        return patientMapper.toResponse(
                repository.save(patient)
        );
    }

    //    tao new user co lien ket voi tai khoan
    @Override
    public PatientResponse createPatient(PatientRequest request, User user) {
        Patient patient = buildPatient(request);
        Relationship relationship = this.buildRelationship(patient, user, null);
        relationship.setVerified(true);
        patient.getRelationships().add(relationship);
        return patientMapper.toResponse(
                repository.save(patient)
        );
    }

    @Override
    public void delete(Integer id) {
        Patient patient = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        repository.delete(patient);
    }

    //    tao user va account
    @Override
    public PatientResponse createPatientAndAccount(PatientRequest request) {
        Patient patient = buildPatient(request);

        if (request.getPhone() != null) {
            User newUser = userService.createAccountByPhone(request.getPhone());

            Relationship relationship = this.buildRelationship(patient, newUser, "Bản thân");
            relationship.setVerified(true);

            patient.getRelationships().add(relationship);
        }

        if (request.getPhoneLink() != null) {
            User userLink = userService.createAccountByPhone(request.getPhoneLink());

            Relationship relationship = this.buildRelationship(patient, userLink, "Người thân");
            relationship.setVerified(true);

            patient.getRelationships().add(relationship);
        }

        return patientMapper.toResponse(
                repository.save(patient)
        );
    }


    @Override
    public Patient findByPhone(String phone) {
        return repository.findByPhone(phone).orElse(null);
    }

    @Override
    public Patient findByCccd(String cccd) {
        return repository.findByCccd(cccd).orElseThrow(() -> new RuntimeException("Patient not found"));
    }


    @Override
    public PatientResponse findById(Integer id) {
        return patientMapper.toResponse(
                repository.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"))
        );
    }

    @Override
    public List<PatientResponse> findAll(String keyword) {
        Specification<Patient> spec = FilterHelper.contain(keyword, List.of(
                "phone", "cccd", "fullName"
        ));
        return repository.findAll(spec).stream()
                .map(patientMapper::toResponse)
                .toList();
    }

    @Override
    public Page<PatientResponse> findAll(String keyword, Pageable pageable) {
        Specification<Patient> spec = FilterHelper.contain(keyword, List.of(
                "phone", "cccd", "fullName"
        ));
        return repository.findAll(spec, pageable)
                .map(patientMapper::toResponse);
    }

    @Override
    public PatientResponse me() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Patient patient = repository.findByPhone(user.getPhone()).orElseThrow(() -> new RuntimeException("Patient not found"));
        PatientResponse response = patientMapper.toResponse(patient);
        response.setEmail(user.getEmail());
        return response;
    }

    @Override
    public Patient save(Patient patient) {
        return repository.save(patient);
    }

    @Override
    public List<PatientResponse> all() {
        User user = userService.getCurrentUser();
        if (user != null) {
            List<Relationship> relationships = user.getRelationships();

            List<PatientResponse> patientResponses = new ArrayList<>();

            for (Relationship relationship : relationships) {
                Patient patient = relationship.getPatient();
                if (user.getPhone().equals(patient.getPhone())) {
                    continue;
                }
                PatientResponse response = patientMapper.toResponse(patient);
                response.setRelationship(relationship.getRelational());
                response.setVerified(relationship.getVerified());
                patientResponses.add(response);
            }
            return patientResponses;
        }
        return null;
    }

    @Override
    public void addRelationship(PatientRequest request) {
//      tao benh nhan tam
        User user = userService.getCurrentUser();
        Patient patient = this.buildPatient(request);
        Relationship relationship = this.buildRelationship(patient, user, request.getRelationshipName());

        relationship.setVerified(false);
        patient.getRelationships().add(relationship);
        repository.save(patient);

//      gui otp
        sendMessage.sendOtp(new OtpRequest(request.getSync(), "null"));
    }

    @Override
    public void syncRelationship(VerifyOtpRequest request) {
//      kiem tra otp
        sendMessage.checkOtp(request.getPhone(), request.getOtp());

//      gan id benh nhan da tim thay vao tai khoan
        User user = userService.getCurrentUser();
        Patient patient = repository.findBySync(request.getPhone()).orElseThrow(() -> new RuntimeException("Patient not found"));
        Patient existPatient = repository.findByPhone(request.getPhone()).orElse(null);

//      dong bo voi benh nhan da ton tai
        if (existPatient != null) {
            String relational = patient.getRelationships().get(0).getRelational();
            Relationship relationship = this.buildRelationship(existPatient, user, relational);
            relationship.setVerified(true);
            existPatient.getRelationships().add(relationship);
            repository.save(existPatient);
            repository.delete(patient);
            return;
        }

//      cap nhat benh nhan moi tao voi so chinh thuc
        patient.getRelationships().get(0).setVerified(true);
        patient.setPhone(request.getPhone());
        repository.save(patient);
    }

    @Override
    public void deleteRelationship(Integer patientId) {
        User user = userService.getCurrentUser();
        user.getRelationships().removeIf(r -> r.getPatient().getId().equals(patientId));
        userService.save(user);
    }

    @Override
    public Relationship buildRelationship(Patient patient, User user, String relational) {
        Relationship relationship = new Relationship();
        relationship.setUser(user);
        relationship.setPatient(patient);
        if (relational != null && !relational.isEmpty()) {
            relationship.setRelational(relational);
        }
        return relationship;
    }

    private Patient buildPatient(PatientRequest request) {
        Patient patient = patientMapper.toEntity(request);
        patient.setCode("BN" + System.currentTimeMillis());
        return patient;
    }


}
