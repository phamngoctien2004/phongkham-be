package com.dcm.demo.service.impl;

import com.dcm.demo.model.HealthPlanParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthPlanParamRepository extends JpaRepository<HealthPlanParam, Integer> {
    void deleteByIdIn(List<Integer> ids);
}
