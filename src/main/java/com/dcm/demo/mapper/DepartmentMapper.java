package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.DepartmentRequest;
import com.dcm.demo.dto.response.DepartmentResponse;
import com.dcm.demo.model.Department;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentResponse toResponse(Department department);
    Department toEntity(DepartmentRequest request);
}
