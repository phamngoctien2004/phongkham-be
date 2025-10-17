package com.dcm.demo.repository;

import com.dcm.demo.model.LabResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabResultDetailRepository extends JpaRepository<LabResultDetail, Integer> {
}
