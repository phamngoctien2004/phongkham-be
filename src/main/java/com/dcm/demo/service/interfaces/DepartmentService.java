package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.response.DepartmentResponse;
import com.dcm.demo.model.Department;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAllDepartment();
}
