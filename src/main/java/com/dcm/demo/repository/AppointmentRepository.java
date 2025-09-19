package com.dcm.demo.repository;

import com.dcm.demo.dto.response.AppointmentResponse;
import com.dcm.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>, JpaSpecificationExecutor<Appointment> {
    List<Appointment> findByPhoneAndStatus(String phone, Appointment.AppointmentStatus status);
    List<Appointment> findByDateAndStatus(LocalDate localDate, Appointment.AppointmentStatus status);
}
