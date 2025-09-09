package com.dcm.demo.repository;

import com.dcm.demo.model.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Integer> {


}
