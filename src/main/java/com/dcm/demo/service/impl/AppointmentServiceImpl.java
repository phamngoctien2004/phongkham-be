package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.mapper.AppointmentMapper;
import com.dcm.demo.model.Appointment;
import com.dcm.demo.repository.AppointmentRepository;
import com.dcm.demo.service.interfaces.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    @Override
    public String createAppointment(AppointmentRequest request) {
        Appointment appointment = mapper.toEntity(request);
        if(request.getExamId() != null){
//           tim goi kham theo id
        }else if(request.getDoctorId() != null){
//           tu gan khoa va bac si
        }else{
//
        }
        return "Appointment created successfully";
    }
}
