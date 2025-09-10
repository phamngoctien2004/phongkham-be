package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.repository.PatientRepository;
import com.dcm.demo.service.interfaces.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository repository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
//        if(request.get == null) {}
//        UserRequest userRequest = request.getUser();
//        User newUser = userMapper.toEntity(userRequest);
//        Patient
        return null;
    }
}
