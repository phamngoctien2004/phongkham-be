package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.DoctorRequest;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface DoctorMapper extends  BaseMapper<Doctor, DoctorRequest, DoctorResponse> {
}
