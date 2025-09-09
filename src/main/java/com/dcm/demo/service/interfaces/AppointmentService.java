package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.AppointmentRequest;

public interface AppointmentService {
    void createAppointment(AppointmentRequest request);
}
