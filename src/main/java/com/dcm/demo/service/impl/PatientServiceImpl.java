package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.mapper.PatientMapper;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.model.Patient;
import com.dcm.demo.model.Relationship;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.PatientRepository;
import com.dcm.demo.service.interfaces.PatientService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
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
        if (request.getUserId() != null) {
            User user = userService.getById(request.getUserId());
            if (user != null) {
                return createPatient(request, user);
            }
        }

        return createPatientAndAccount(request);
    }

    //    tao new user co lien ket voi tai khoan
    @Override
    public PatientResponse createPatient(PatientRequest request, User user) {
        return patientMapper.toResponse(
                repository.save(buildPatient(request, user))
        );
    }

    //    tao user va account
    @Override
    public PatientResponse createPatientAndAccount(PatientRequest request) {
        User UserExist = userService.findByPhone(request.getPhone());
        if (UserExist != null) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(User.Role.BENH_NHAN)
                .build();
        User newUser = userService.save(user);

        return patientMapper.toResponse(
                repository.save(buildPatient(request, newUser))
        );
    }

    private Patient buildPatient(PatientRequest request, User user) {
        Patient patient = patientMapper.toEntity(request);
        patient.setCode("BN" + System.currentTimeMillis());
        Relationship relationship = new Relationship();
        relationship.setUser(user);
        relationship.setPatient(patient);
        patient.getRelationships().add(relationship);
        return patient;
    }

    @Override
    @Transactional
    public List<PatientResponse> findAllPatientByPhone(String phone) {
        User user = userService.findByPhone(phone);
        if (user != null) {
            List<Relationship> relationships = user.getRelationships();

            return relationships.stream()
                    .map((it) -> {
                        PatientResponse response = patientMapper.toResponse(it.getPatient());
                        response.setRelationship(it.getRelational());
                        return response;
                    }).toList();
        }
        return List.of();
    }
}
