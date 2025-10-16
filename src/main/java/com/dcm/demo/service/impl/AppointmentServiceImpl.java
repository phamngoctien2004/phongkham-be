package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.response.*;
import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.mapper.AppointmentMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.AppointmentRepository;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final HealthPlanService healthPlanService;
    private final PatientService patientService;
    private final EmailService emailService;

    @Override
    @Async
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

        Integer patientId = request.getPatientId();

        if (request.getPatientId() == null) {
//          kiem tra tai khoan da ton tai
            Patient patient = patientService.findByPhone(request.getPhone());
            if (patient == null) {
                PatientResponse patientResponse = patientService.createPatientAndAccount(buildPatientRequest(request));
                patientId = patientResponse.getId();
            } else {
                patientId = patient.getId();
            }
            sendEmailAppointmentSuccess(request.getPhone(), request.getEmail());
        }
        Patient patient = new Patient();
        patient.setId(patientId);
        appointment.setPatient(patient);
        appointment.setStatus(Appointment.AppointmentStatus.DA_XAC_NHAN);
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
    public Page<AppointmentResponse> findByPhone(String phone, LocalDate date, Appointment.AppointmentStatus status, Pageable pageable) {
        Specification<Appointment> spec = FilterHelper.contain(phone, List.of(
                "phone"
        ));
        if(date != null) {
            spec = spec.and(FilterHelper.equal("date", date));
        }
        if(status != null) {
            spec = spec.and(FilterHelper.equal("status", status));
        }
        Page<Appointment> appointments = repository.findAll(spec, pageable);

        return appointments.map((appointment -> {
            AppointmentResponse response = mapper.toResponse(appointment);
            response.setStatus(appointment.getStatus());
            response.setPatientId(appointment.getPatient().getId());
            if (appointment.getDoctor() != null) {
                Doctor doctor = appointment.getDoctor();
                DoctorResponse doctorResponse = new DoctorResponse();
                doctorResponse.setId(doctor.getId());
                doctorResponse.setPosition(doctor.getPosition());
                response.setDoctorResponse(doctorResponse);
            }

            if (appointment.getHealthPlan() != null) {
                HealthPlan healthPlan = appointment.getHealthPlan();
                HealthPlanResponse healthPlanResponse = new HealthPlanResponse();
                healthPlanResponse.setId(healthPlan.getId());
                healthPlanResponse.setName(healthPlan.getName());
                response.setHealthPlanResponse(healthPlanResponse);
            }

            return response;
        }));
    }

    @Override
    public List<AppointmentResponse> findAllByDate() {
        return List.of();
    }

    private PatientRequest buildPatientRequest(AppointmentRequest request) {
        PatientRequest patientRequest = new PatientRequest();
        patientRequest.setFullName(request.getFullName());
        patientRequest.setPhone(request.getPhone());
        patientRequest.setEmail(request.getEmail());
        patientRequest.setAddress(request.getAddress());
        patientRequest.setGender(request.getGender());
        patientRequest.setBirth(request.getBirth());
        return patientRequest;
    }

    void sendEmailAppointmentSuccess(String phone, String email) {
        String subject = "Appointment Confirmation";
        String template = "welcome";
        Map<String, Object> dataContext = Map.of(
                "phoneNumber", phone
        );
        emailService.sendTemplate(email, subject, template, dataContext);
    }


}
