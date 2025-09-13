package com.dcm.demo.repository;

import com.dcm.demo.model.ScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, Integer> {
    List<ScheduleException> findByDateInAndLeaveStatus(Collection<LocalDate> dates, ScheduleException.leaveStatus leaveStatus);
    List<ScheduleException> findByDateAndDoctorId(LocalDate date, Integer id);

}
