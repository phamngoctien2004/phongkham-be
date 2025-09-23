package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.MedicalResponse;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.model.MedicalRecord;
import com.dcm.demo.model.Patient;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface MedicalMapper extends BaseMapper<MedicalRecord, MedicalRequest, MedicalResponse> {
}
