package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.DepartmentResponse;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.mapper.DepartmentMapper;
import com.dcm.demo.mapper.DoctorMapper;
import com.dcm.demo.model.Department;
import com.dcm.demo.repository.DepartmentRepository;
import com.dcm.demo.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;
    private final DoctorMapper doctorMapper;
    @Override
    public List<DepartmentResponse> getAllDepartment() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public Department findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<DepartmentResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<DoctorResponse> findDoctorByDepartments(Integer id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department Not Found"));
        return department.getDoctors().stream()
                .map(it -> {
                    DoctorResponse response = new DoctorResponse();
                    response.setId(it.getId());
                    response.setPosition(it.getPosition());
                    return response;
                })
                .toList();
    }
}
