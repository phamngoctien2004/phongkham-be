package com.dcm.demo.repository;

import com.dcm.demo.model.LabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, Integer> {
    Optional<LabOrder> findByMedicalRecordCode (String code);
}
