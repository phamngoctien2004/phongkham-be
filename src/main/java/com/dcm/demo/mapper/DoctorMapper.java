package com.dcm.demo.mapper;

import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.model.Doctor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorResponse toResponse(Doctor doctor);
}
