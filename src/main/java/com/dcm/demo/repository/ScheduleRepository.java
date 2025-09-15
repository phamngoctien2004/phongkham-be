package com.dcm.demo.repository;

import com.dcm.demo.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByDayAndDoctorId(DayOfWeek dayOfWeek, Integer id);

    Optional<Schedule> findByDayAndDoctorIdAndStartTimeAndEndTime(DayOfWeek dayOfWeek, Integer id, LocalTime startTime, LocalTime endTime);


    @Query("""
                SELECT s FROM Schedule s
                JOIN FETCH s.doctor d
                WHERE (:doctorId IS NULL OR d.id = :doctorId)
                  AND (:departmentId IS NULL OR s.departmentId = :departmentId)
                  AND (:day IS NULL OR s.day = :day)
                  AND (:shift IS NULL OR s.shift = :shift)
            """)
    List<Schedule> findByOption(
            Integer doctorId,
            Integer departmentId,
            DayOfWeek day,
            Schedule.Shift shift
    );
}
