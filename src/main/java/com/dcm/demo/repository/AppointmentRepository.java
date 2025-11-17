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
public interface AppointmentRepository
        extends JpaRepository<Appointment, Integer>, JpaSpecificationExecutor<Appointment> {

    List<Appointment> findByDate(LocalDate date);

    List<Appointment> findByDoctorIdAndDateAndTimeIsBetweenAndStatusIn(Integer doctor_id, LocalDate date,
                                                                       LocalTime to, LocalTime end, Collection<Appointment.AppointmentStatus> status);

    @Query("""
                    SELECT a FROM Appointment a
                    JOIN a.patient p
                    WHERE p.id = :patientId
                    AND (:date IS NULL OR a.date = :date)
                    AND (:status IS NULL OR a.status = :status)
            """)
    Page<Appointment> findMyAppointment(Integer patientId, LocalDate date, Appointment.AppointmentStatus status,
                                        Pageable pageable);

    // Report queries
    @Query("""
            SELECT COUNT(a) FROM Appointment a
            WHERE a.date BETWEEN :fromDate AND :toDate
            AND (:status IS NULL OR a.status = :status)
            """)
    Long countByDateRangeAndStatus(LocalDate fromDate, LocalDate toDate, Appointment.AppointmentStatus status);

    @Query("""
            SELECT d.id, d.fullName, dep.name, COUNT(a),
            SUM(CASE WHEN a.status = com.dcm.demo.model.Appointment.AppointmentStatus.HOAN_THANH THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.dcm.demo.model.Appointment.AppointmentStatus.HUY THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.dcm.demo.model.Appointment.AppointmentStatus.KHONG_DEN THEN 1 ELSE 0 END)
            FROM Appointment a
            JOIN a.doctor d
            LEFT JOIN d.department dep
            WHERE a.date BETWEEN :fromDate AND :toDate
            GROUP BY d.id, d.fullName, dep.name
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> getAppointmentsByDoctor(LocalDate fromDate, LocalDate toDate);

    @Query("""
            SELECT dep.id, dep.name, COUNT(a),
            SUM(CASE WHEN a.status = com.dcm.demo.model.Appointment.AppointmentStatus.HOAN_THANH THEN 1 ELSE 0 END)
            FROM Appointment a
            JOIN a.doctor d
            JOIN d.department dep
            WHERE a.date BETWEEN :fromDate AND :toDate
            GROUP BY dep.id, dep.name
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> getAppointmentsByDepartment(LocalDate fromDate, LocalDate toDate);

    @Query("""
            SELECT a.date, COUNT(a),
            SUM(CASE WHEN a.status = com.dcm.demo.model.Appointment.AppointmentStatus.HOAN_THANH THEN 1 ELSE 0 END)
            FROM Appointment a
            WHERE a.date BETWEEN :fromDate AND :toDate
            GROUP BY a.date
            ORDER BY a.date
            """)
    List<Object[]> getAppointmentsByDay(LocalDate fromDate, LocalDate toDate);

    @Query("""
            SELECT d.id, d.fullName, dep.name, COUNT(a),
            SUM(CASE WHEN a.status = com.dcm.demo.model.Appointment.AppointmentStatus.HOAN_THANH THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.dcm.demo.model.Appointment.AppointmentStatus.HUY THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.dcm.demo.model.Appointment.AppointmentStatus.KHONG_DEN THEN 1 ELSE 0 END),
            COUNT(DISTINCT a.patient.id)
            FROM Appointment a
            JOIN a.doctor d
            LEFT JOIN d.department dep
            WHERE a.date BETWEEN :fromDate AND :toDate
            GROUP BY d.id, d.fullName, dep.name
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> getDoctorPerformance(LocalDate fromDate, LocalDate toDate);
}
