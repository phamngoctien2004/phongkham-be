package com.dcm.demo.repository;

import com.dcm.demo.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByDayAndDoctorId(Schedule.DayOfWeek dayOfWeek, Integer id);
}
