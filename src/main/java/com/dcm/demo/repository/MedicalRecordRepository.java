package com.dcm.demo.repository;

import com.dcm.demo.model.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer>, JpaSpecificationExecutor<MedicalRecord> {
    List<MedicalRecord> findByPatientIdOrderByDateDesc(Integer id);

    @Query(value = """
                SELECT mr FROM MedicalRecord mr
                LEFT JOIN FETCH mr.patient p
                LEFT JOIN FETCH mr.healthPlan hl
                LEFT JOIN FETCH hl.room r
                WHERE (:keyword IS NULL OR :keyword = '' 
                       OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) 
                       OR LOWER(mr.code) LIKE LOWER(CONCAT('%', :keyword, '%')) )
                  AND (:status IS NULL OR mr.status = :status)
                  AND ( (:from IS NULL OR :to IS NULL) OR (mr.date >= :from AND mr.date < :to) )
                  AND (:doctorId IS NULL OR mr.doctor.id = :doctorId) 
                  AND(:departmentId IS NULL OR r.department.id = :departmentId)
                ORDER BY mr.date DESC
            """)
    Page<MedicalRecord> findAll(
            @Param("keyword") String keyword,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") MedicalRecord.RecordStatus status,
            @Param("doctorId") Integer doctorId,
            @Param("departmentId") Integer departmentId,
            Pageable pageable);

    List<MedicalRecord> findByPatientId(Integer patientId);
}
