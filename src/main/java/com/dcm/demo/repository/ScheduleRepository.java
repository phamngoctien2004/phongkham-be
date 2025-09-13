package com.dcm.demo.repository;

import com.dcm.demo.dto.request.FilterSlotRequest;
import com.dcm.demo.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByDayAndDoctorId(Schedule.DayOfWeek dayOfWeek, Integer id);
    Optional<Schedule> findByDayAndDoctorIdAndStartTimeAndEndTime(Schedule.DayOfWeek dayOfWeek, Integer id, LocalTime startTime, LocalTime endTime);
    List<Schedule> findByDayIn(Set<Schedule.DayOfWeek> days);

    @Query("""
        SELECT s FROM Schedule s
        JOIN s.doctor d
        WHERE (:doctorId IS NULL OR d.id = :doctorId)
          AND (:departmentId IS NULL OR s.departmentId = :departmentId)
          AND (:day IS NULL OR s.day = :day)
          AND (s.status = true)
    """)
    List<Schedule> findByOption(
        Integer doctorId,
        Integer departmentId,
        Schedule.DayOfWeek day
    );
}
