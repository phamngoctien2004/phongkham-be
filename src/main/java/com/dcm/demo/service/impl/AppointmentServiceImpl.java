package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.mapper.AppointmentMapper;
import com.dcm.demo.model.Appointment;
import com.dcm.demo.model.Department;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.repository.AppointmentRepository;
import com.dcm.demo.service.interfaces.AppointmentService;
import com.dcm.demo.service.interfaces.DepartmentService;
import com.dcm.demo.service.interfaces.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    @Override
    public void createAppointment(AppointmentRequest request) {
        Appointment appointment = mapper.toEntity(request);
        if(request.getDoctorId() != null){
            Doctor doctor = doctorService.findById(request.getDoctorId());
            Department department = doctor.getDepartment();
            appointment.setDoctor(doctor);
            appointment.setDepartment(department);
        }else{
            Department department = departmentService.findById(request.getDepartmentId());
            appointment.setDepartment(department);
        }
        System.out.println("ok");
    }
}
