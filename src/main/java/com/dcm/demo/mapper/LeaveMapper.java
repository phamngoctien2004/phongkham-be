package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.DoctorRequest;
import com.dcm.demo.dto.request.LeaveRequest;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.LeaveResponse;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Leave;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface LeaveMapper extends  BaseMapper<Leave, LeaveRequest, LeaveResponse> {

}
