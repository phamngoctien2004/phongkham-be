package com.dcm.demo.repository;

import com.dcm.demo.model.LabOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, Integer> , JpaSpecificationExecutor<LabOrder> {
    Optional<LabOrder> findByMedicalRecordCode (String code);


    @Query("""
                SELECT l FROM LabOrder l
                JOIN FETCH l.healthPlan h 
                JOIN FETCH l.medicalRecord mr
                JOIN FETCH l.performingDoctor d
                where (:keyword IS NULL OR :keyword = ''
                OR LOWER (mr.code) LIKE LOWER (CONCAT('%', :keyword, '%')) )
                AND (:status IS NULL OR l.status = :status)
                AND ( (:from IS NULL OR :to IS NULL) OR (l.orderDate >= :from AND l.orderDate < :to) )
                AND (:doctorId IS NULL OR (d.id = :doctorId) )
                AND (h.id != 1)
                ORDER BY l.orderDate ASC 
            """)
    List<LabOrder> findAll(
            @Param("doctorId") Integer doctorId,
            @Param("keyword") String keyword,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") LabOrder.TestStatus status);

    @Query("""
                SELECT l FROM LabOrder l
                JOIN FETCH l.healthPlan h 
                JOIN FETCH l.medicalRecord mr
                JOIN FETCH h.room r
                where (:keyword IS NULL OR :keyword = ''
                OR LOWER (mr.code) LIKE LOWER (CONCAT('%', :keyword, '%')) )
                AND (:status IS NULL OR l.status = :status)
                AND ( (:from IS NULL OR :to IS NULL) OR (l.orderDate >= :from AND l.orderDate < :to) )
                AND (:departmentId IS NULL OR (r.department.id = :departmentId) )
                AND (h.id != 1)
            """)
    Page<LabOrder> findAllWithPagination(
            @Param("departmentId") Integer departmentId,
            @Param("keyword") String keyword,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") LabOrder.TestStatus status,
            Pageable pageable);


    List<LabOrder> findByMedicalRecordIdAndHealthPlanIdIn(Integer medicalRecordId, List<Integer> healthPlanIds);
    Optional<LabOrder> findByMedicalRecordIdAndHealthPlanId(Integer medicalRecordId, Integer healthPlanId);
}
