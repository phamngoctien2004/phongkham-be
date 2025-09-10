package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.model.Appointment;

public interface AppointmentService {
    void createAppointment(AppointmentRequest request);
    String changeStatusAppointment(Integer appointmentId, Appointment.AppointmentStatus status);
    Appointment findById(Integer id);
}
