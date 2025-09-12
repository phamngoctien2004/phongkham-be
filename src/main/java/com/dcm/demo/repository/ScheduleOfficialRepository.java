package com.dcm.demo.repository;

import com.dcm.demo.model.ScheduleOfficial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleOfficialRepository extends JpaRepository<ScheduleOfficial, Integer> {
    List<ScheduleOfficial> findByDateAndDoctorId(LocalDate date, Integer doctorId);
}
