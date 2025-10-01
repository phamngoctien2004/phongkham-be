package com.dcm.demo.repository;

import com.dcm.demo.model.HealthPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationServiceRepository extends JpaRepository<HealthPlan, Integer>, JpaSpecificationExecutor<HealthPlan> {
    List<HealthPlan> findAllByIdIn(List<Integer> ids);

}
