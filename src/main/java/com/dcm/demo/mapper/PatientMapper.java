package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.model.Patient;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toEntity(PatientRequest patientRequest);
}
