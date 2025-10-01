package com.dcm.demo.repository;

import com.dcm.demo.model.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {
//        @Query("""
//                SELECT ivd.healthPlan.id FROM InvoiceDetail ivd
//                LEFT JOIN ivd.invoice iv
//                WHERE iv.medicalRecord.id = :medicalRecordId
//                AND ivd.status = :status
//                """)
//    List<Integer> findServicesUnpay(@Param("medicalRecordId") Integer medicalRecordId,
//                                    @Param("status") InvoiceDetail.Status status);
@Query("""
            SELECT ivd FROM InvoiceDetail ivd
            LEFT JOIN ivd.invoice iv
            WHERE iv.medicalRecord.id = :medicalRecordId
            """)
List<InvoiceDetail> findByRecordId(@Param("medicalRecordId") Integer medicalRecordId);

    @Query("""
            SELECT ivd.healthPlan.id FROM InvoiceDetail ivd
            LEFT JOIN ivd.invoice iv
            WHERE iv.medicalRecord.id = :medicalRecordId
            AND ivd.status = 'CHUA_THANH_TOAN'
            """)
    List<Integer> findServicesUnpay(@Param("medicalRecordId") Integer medicalRecordId);
}
