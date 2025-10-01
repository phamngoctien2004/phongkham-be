package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.dto.response.PatientsDto;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PatientMapper patientMapper;

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
            User user = userService.findByPhone(patient.getPhone());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            userService.save(user);
        }
        return patientMapper.toResponse(
                repository.save(patient)
        );
    }

    //    tao new user co lien ket voi tai khoan
    @Override
    public PatientResponse createPatient(PatientRequest request, User user) {
        Patient patient = buildPatient(request);
        this.buildRelationship(patient, user);
        return patientMapper.toResponse(
                repository.save(patient)
        );
    }

    //    tao user va account
    @Override
    public PatientResponse createPatientAndAccount(PatientRequest request) {
//        User UserExist = userService.findByPhone(phoneCreateAccount);
//        if (UserExist != null) {
//            throw new RuntimeException("Số điện thoại đã tồn tại");
//        }
//        User user = User.builder()
//                .email(request.getEmail())
//                .phone(phoneCreateAccount)
//                .role(User.Role.BENH_NHAN)
//                .build();
//        User newUser = userService.save(user);

        Patient patient = buildPatient(request);

        if (request.getPhone() != null) {
            User newUser = userService.createAccountByPhone(request.getPhone());
            this.buildRelationship(patient, newUser);
        }

        if (request.getPhoneLink() != null) {
            User userLink = userService.createAccountByPhone(request.getPhoneLink());
            this.buildRelationship(patient, userLink);
        }

        return patientMapper.toResponse(
                repository.save(patient)
        );
    }

    private Patient buildPatient(PatientRequest request) {
        Patient patient = patientMapper.toEntity(request);
        patient.setCode("BN" + System.currentTimeMillis());
        return patient;
    }

    private void buildRelationship(Patient patient, User user) {
        Relationship relationship = new Relationship();
        relationship.setUser(user);
        relationship.setPatient(patient);
        patient.getRelationships().add(relationship);
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
    public List<PatientResponse> all() {
        User user = userService.getCurrentUser();
        if (user != null) {
            List<Relationship> relationships = user.getRelationships();

            PatientsDto patientsDto = new PatientsDto();

            return relationships.stream()
                    .map((it) -> {
                        Patient patient = it.getPatient();
                        if (!user.getPhone().equals(patient.getPhone())) {
                            PatientResponse response = patientMapper.toResponse(patient);
                            response.setRelationship(it.getRelational());
                            return response;
                        }
                        return null;
                    }).toList();
        }
        return null;
    }


}
