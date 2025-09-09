package com.dcm.demo.repository;

import com.dcm.demo.model.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Integer> {

}
