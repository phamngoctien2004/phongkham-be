package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.mapper.AppointmentMapper;
import com.dcm.demo.model.Appointment;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.HealthPlan;
import com.dcm.demo.repository.AppointmentRepository;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if(request.getHealthPlanId() != null){
            HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
            appointment.setHealthPlan(healthPlan);
        }
        if(request.getDoctorId() != null){
            Doctor doctor = doctorService.findById(request.getDoctorId());
            appointment.setDoctor(doctor);
        }

        repository.save(appointment);
        System.out.println("ok");
    }

    @Override
    public String changeStatusAppointment(Integer appointmentId, Appointment.AppointmentStatus status) {
        Appointment appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(status);

//      nguoi xac nhan (khi nao co jwt moi chay duoc)
//        User user = userService.getCurrentUser();
//        appointment.setConfirmedBy(user);

        repository.save(appointment);
        if("DA_XAC_NHAN".equals(status.name())){
            return "Appointment confirmed successfully";
        }
        if("HOAN_THANH".equals(status.name())){
            return "Appointment completed successfully";
        }
        return "Appointment cancelled successfully";
    }

    @Override
    public Appointment findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
    }
}
