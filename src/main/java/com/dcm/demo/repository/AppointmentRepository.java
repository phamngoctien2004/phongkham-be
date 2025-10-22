package com.dcm.demo.repository;

import com.dcm.demo.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>, JpaSpecificationExecutor<Appointment> {
    List<Appointment> findByDoctorIdAndDateAndTimeIsBetweenAndStatusIn(Integer doctor_id, LocalDate date, LocalTime to, LocalTime end, Collection<Appointment.AppointmentStatus> status);

    @Query("""
        SELECT a FROM Appointment a
        JOIN a.patient p
        WHERE p.id = :patientId
        AND (:date IS NULL OR a.date = :date)
        AND (:status IS NULL OR a.status = :status)

""")
    Page<Appointment> findMyAppointment(Integer patientId, LocalDate date, Appointment.AppointmentStatus status, Pageable pageable);
}
