package com.dcm.demo.repository;

import com.dcm.demo.model.ScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends JpaRepository<ScheduleException, Integer> {


}
