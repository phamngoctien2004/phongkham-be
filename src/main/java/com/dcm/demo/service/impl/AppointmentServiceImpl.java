package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.dto.response.AppointmentResponse;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.HealthPlanResponse;
import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.mapper.AppointmentMapper;
import com.dcm.demo.mapper.PatientMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.AppointmentRepository;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final DoctorService doctorService;
    private final HealthPlanService healthPlanService;
    private final PatientService patientService;
    private final EmailService emailService;
    private final PatientMapper patientMapper;
    private final UserService userService;
    private final InvoiceService invoiceService;

    @Override
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Appointment appointment = mapper.toEntity(request);
//
//        if (request.getHealthPlanId() != null) {
//            HealthPlan healthPlan = healthPlanService.findById(request.getHealthPlanId());
//            appointment.setTotalAmount(healthPlan.getPrice());
//            appointment.setHealthPlan(healthPlan);
//        }
//        if (request.getDoctorId() != null) {
//            Doctor doctor = doctorService.findById(request.getDoctorId());
//            Degree degree = doctor.getDegree();
//            appointment.setTotalAmount(degree.getExaminationFee());
//            appointment.setDoctor(doctor);
//        }

        Patient patient = patientService.findEntityById(request.getPatientId());
        appointment.setPatient(patient);
        appointment.setStatus(Appointment.AppointmentStatus.CHO_THANH_TOAN);
        repository.save(appointment);
        return this.toResponse(appointment);
    }

    @Override
    public AppointmentResponse getAppointmentResponse(Integer id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return this.toResponse(appointment);
    }

    @Override
    public List<LocalTime> getTimeBooked(Integer doctorId, LocalDate date, Schedule.Shift shift) {
        LocalTime to, end;
        if (shift == Schedule.Shift.SANG) {
            to = LocalTime.of(6, 0);
            end = LocalTime.of(12, 0);
        } else if (shift == Schedule.Shift.CHIEU) {
            to = LocalTime.of(12, 0);
            end = LocalTime.of(17, 0);
        } else {
            to = LocalTime.of(17, 0);
            end = LocalTime.of(23, 0);
        }


        return repository.findByDoctorIdAndDateAndTimeIsBetweenAndStatusIn(doctorId, date, to, end, List.of(
                        Appointment.AppointmentStatus.CHO_THANH_TOAN,
                        Appointment.AppointmentStatus.DA_XAC_NHAN
                )).stream()
                .map(Appointment::getTime)
                .toList();
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        AppointmentResponse response = mapper.toResponse(appointment);
        response.setPatientResponse(patientMapper.toResponse(appointment.getPatient()));
        if (appointment.getDoctor() != null) {
            Doctor doctor = appointment.getDoctor();
            DoctorResponse doctorResponse = new DoctorResponse();
            doctorResponse.setFullName(doctor.getFullName());
            doctorResponse.setId(doctor.getId());
            doctorResponse.setPosition(doctor.getPosition());
            response.setDoctorResponse(doctorResponse);
        } else {
            HealthPlan healthPlan = appointment.getHealthPlan();
            HealthPlanResponse healthPlanResponse = new HealthPlanResponse();
            healthPlanResponse.setId(healthPlan.getId());
            healthPlanResponse.setName(healthPlan.getName());
            response.setHealthPlanResponse(healthPlanResponse);
        }

        return response;
    }

    @Override
    public AppointmentResponse update(AppointmentRequest request) {
        Appointment appointment = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (request.getPatientId() != null) {
            Patient patient = new Patient();
            patient.setId(request.getPatientId());
            appointment.setPatient(patient);
        }
        appointment.setDate(request.getDate());
        appointment.setTime(request.getTime());
        appointment.setSymptoms(request.getSymptoms());
        return this.toResponse(appointment);
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
        if (date != null) {
            spec = spec.and(FilterHelper.equal("date", date));
        }
        if(status == null){
            spec = spec.and(FilterHelper.equal("status", Appointment.AppointmentStatus.DA_XAC_NHAN));
        }
        if (status != null) {
            spec = spec.and(FilterHelper.equal("status", status));
        }
        Page<Appointment> appointments = repository.findAll(spec, pageable);

        return appointments.map((appointment -> {
            AppointmentResponse response = this.toResponse(appointment);
            response.setStatus(appointment.getStatus());
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
    public Page<AppointmentResponse> findMyAppointment(LocalDate date, Appointment.AppointmentStatus status, Pageable pageable) {
        User user = userService.getCurrentUser();
        Patient patient = patientService.findByPhone(user.getPhone());

        return repository.findMyAppointment(patient.getId(), date, status, pageable).map((appointment -> {
            AppointmentResponse response = this.toResponse(appointment);
            response.setStatus(appointment.getStatus());
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

    @Override
    public void save(Appointment appointment) {
        repository.save(appointment);
    }

    @Override
    public boolean checkPayment(Integer id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
//        Invoice invoice = invoiceService.findByCode(appointment.getInvoiceCode());

        return false;
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
