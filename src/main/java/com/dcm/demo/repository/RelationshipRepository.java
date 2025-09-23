package com.dcm.demo.repository;

import com.dcm.demo.model.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Integer> {
    Optional<Relationship> findByPatientIdAndUserId(Integer patientId, Integer userId);
}
