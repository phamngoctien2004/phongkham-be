package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.DepartmentRequest;
import com.dcm.demo.dto.response.DepartmentResponse;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {

    Page<DepartmentResponse> getAllDepartment(Pageable pageable, String keyword);

    DepartmentResponse findById(Integer id);

    List<DepartmentResponse> findAll();

    List<DoctorResponse> findDoctorByDepartments(Integer id);

    String getRoomFromDepartment(Department department);

    DepartmentResponse create(DepartmentRequest request);

    DepartmentResponse update(DepartmentRequest request);

    void delete(Integer id);
}
