package com.dcm.demo.repository;

import com.dcm.demo.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    Optional<Prescription> findByMedicalRecordId(Integer medicalRecordId);
}
