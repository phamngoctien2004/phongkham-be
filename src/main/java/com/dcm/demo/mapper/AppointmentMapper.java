package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.model.Appointment;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    Appointment toEntity(AppointmentRequest request);
}
