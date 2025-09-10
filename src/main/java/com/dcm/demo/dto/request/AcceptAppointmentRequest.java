package com.dcm.demo.dto.request;

import com.dcm.demo.model.Appointment;
import lombok.Data;

@Data
public class AcceptAppointmentRequest {
    private Integer id;
    private Appointment.AppointmentStatus status;
}
