package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.DepartmentResponse;
import com.dcm.demo.mapper.DepartmentMapper;
import com.dcm.demo.model.Department;
import com.dcm.demo.repository.DepartmentRepository;
import com.dcm.demo.service.interfaces.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private DepartmentRepository repository;
    private DepartmentMapper mapper;
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
}
