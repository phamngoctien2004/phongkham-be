package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.dto.response.AppointmentResponse;
import com.dcm.demo.dto.response.DepartmentResponse;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.mapper.AppointmentMapper;
import com.dcm.demo.model.Appointment;
import com.dcm.demo.model.Department;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.repository.AppointmentRepository;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final HealthPlanService healthPlanService;
    private final UserService userService;

    @Override
    @Transactional
    public void createAppointment(AppointmentRequest request) {
        Appointment appointment = mapper.toEntity(request);

        if (request.getHealthPlanId() != null) {
            HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
            appointment.setHealthPlan(healthPlan);
        }
        if (request.getDoctorId() != null) {
            Doctor doctor = doctorService.findById(request.getDoctorId());
            appointment.setDoctor(doctor);
        }
        if(request.getDepartmentId() != null) {
            Department department = departmentService.findById(request.getDepartmentId());
            appointment.setDepartment(department);
        }
        appointment.setStatus(Appointment.AppointmentStatus.CHO_XAC_NHAN);
        repository.save(appointment);
        System.out.println("ok");
    }

    @Override
    public String changeStatusAppointment(AppointmentRequest request) {
        Appointment appointment = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(request.getStatus());

//      nguoi xac nhan (khi nao co jwt moi chay duoc)
//        User user = userService.getCurrentUser();
//        appointment.setConfirmedBy(user);

        repository.save(appointment);
        if ("DA_XAC_NHAN".equals(request.getStatus().name())) {
            return "Appointment confirmed successfully";
        }
        if ("HOAN_THANH".equals(request.getStatus().name())) {
            return "Appointment completed successfully";
        }
        return "Appointment cancelled successfully";
    }

    @Override
    public Appointment findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Override
    public List<AppointmentResponse> findByPhone(String phone) {
        List<Appointment> appointments = repository.findByPhoneAndStatus(phone, Appointment.AppointmentStatus.CHO_XAC_NHAN);

        return appointments.stream().map((appointment -> {
            AppointmentResponse response = mapper.toResponse(appointment);

            if(appointment.getDoctor() != null) {
                Doctor doctor = appointment.getDoctor();
                DoctorResponse doctorResponse = new DoctorResponse();
                doctorResponse.setId(doctor.getId());
                doctorResponse.setPosition(doctor.getPosition());
                response.setDoctorResponse(doctorResponse);
            }

            if(appointment.getHealthPlan() != null) {
                HealthPlan healthPlan = appointment.getHealthPlan();
                HealthPlanResponse healthPlanResponse = new HealthPlanResponse();
                healthPlanResponse.setId(healthPlan.getId());
                healthPlanResponse.setName(healthPlan.getName());
                response.setHealthPlanResponse(healthPlanResponse);
            }
            if(appointment.getDepartment() != null) {
                Department department = appointment.getDepartment();
                DepartmentResponse departmentResponse = new DepartmentResponse();
                departmentResponse.setId(department.getId());
                departmentResponse.setName(department.getName());
                response.setDepartmentResponse(departmentResponse);
            }
            return response;
        })).toList();

    }
}
