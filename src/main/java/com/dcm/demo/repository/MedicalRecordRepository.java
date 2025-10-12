package com.dcm.demo.repository;

import com.dcm.demo.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer>, JpaSpecificationExecutor<MedicalRecord> {
    List<MedicalRecord> findByPatientIdOrderByDateDesc(Integer id);

    @Query(value = """
                SELECT mr FROM MedicalRecord mr
                LEFT JOIN FETCH mr.patient p
            
                WHERE (:keyword IS NULL OR :keyword = '' 
                       OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) 
                       OR LOWER(mr.code) LIKE LOWER(CONCAT('%', :keyword, '%')) )
                  AND (:status IS NULL OR mr.status = :status)
                  AND ( (:from IS NULL OR :to IS NULL) OR (mr.date >= :from AND mr.date < :to) )
                ORDER BY mr.date DESC
            """)
    List<MedicalRecord> findAll(
            @Param("keyword") String keyword,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") MedicalRecord.RecordStatus status);

    List<MedicalRecord> findByPatientId(Integer patientId);
}
