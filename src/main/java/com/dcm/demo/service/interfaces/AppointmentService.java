package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.dto.response.AppointmentResponse;
import com.dcm.demo.model.Appointment;
import com.dcm.demo.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    AppointmentResponse getAppointmentResponse(Integer  id);
    List<LocalTime> getTimeBooked(Integer doctorId, LocalDate date, Schedule.Shift shift);
    AppointmentResponse update(AppointmentRequest request);
    String changeStatusAppointment(AppointmentRequest request);
    Appointment findById(Integer id);
    Page<AppointmentResponse> findByPhone(String value, LocalDate date, Appointment.AppointmentStatus status, Pageable pageable);
    Page<AppointmentResponse> findMyAppointment(LocalDate date, Appointment.AppointmentStatus status, Pageable pageable);

    List<AppointmentResponse> findAllByDate();
    void save(Appointment appointment);
    boolean checkPayment(Integer id);
}
