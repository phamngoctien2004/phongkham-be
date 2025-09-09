package com.dcm.demo.repository;

import com.dcm.demo.model.ExaminationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExaminationServiceRepository extends JpaRepository<ExaminationService, Integer> {


}
