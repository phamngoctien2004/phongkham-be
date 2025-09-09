package com.dcm.demo.service.impl;

import com.dcm.demo.model.Doctor;
import com.dcm.demo.repository.DoctorRepository;
import com.dcm.demo.service.interfaces.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository repository;


    @Override
    public Doctor findById(Integer id) {
        return repository.findById(id).orElse(null);
    }
}
