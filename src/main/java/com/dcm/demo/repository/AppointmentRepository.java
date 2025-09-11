package com.dcm.demo.repository;

import com.dcm.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByPhoneAndStatus(String phone, Appointment.AppointmentStatus status);
}
