package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.AppointmentRequest;
import com.dcm.demo.dto.response.AppointmentResponse;
import com.dcm.demo.model.Appointment;

import java.util.List;

public interface AppointmentService {
    void createAppointment(AppointmentRequest request);
    String changeStatusAppointment(AppointmentRequest request);
    Appointment findById(Integer id);
    List<AppointmentResponse> findByPhone(String phone);
}
