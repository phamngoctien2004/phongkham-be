package com.dcm.demo.repository;

import com.dcm.demo.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Integer> {
    List<Leave> findByDateAndDoctorIdAndLeaveStatus(LocalDate date, Integer doctorId, Leave.leaveStatus leaveStatus);

    @Query("""
            SELECT l FROM Leave l
            WHERE (:date IS NULL  OR l.date = :date) 
                        AND (:status IS NULL OR l.leaveStatus = :status)
                        AND (:doctorId IS NULL OR l.doctor.id = :doctorId)
            order by l.submitDate DESC
                        """)
    List<Leave> findByDateAndLeaveStatus(
            @Param("date") LocalDate date,
            @Param("status") Leave.leaveStatus leaveStatus,
            @Param("doctorId") Integer doctorId);
}
