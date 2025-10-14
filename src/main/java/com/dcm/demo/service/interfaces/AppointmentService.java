package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.dto.response.AppointmentResponse;
import com.dcm.demo.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    void createAppointment(AppointmentRequest request);
    String changeStatusAppointment(AppointmentRequest request);
    Appointment findById(Integer id);
    Page<AppointmentResponse> findByPhone(String value, LocalDate date, Appointment.AppointmentStatus status, Pageable pageable);
    List<AppointmentResponse> findAllByDate();
}
