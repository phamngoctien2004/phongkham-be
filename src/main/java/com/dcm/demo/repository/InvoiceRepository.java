package com.dcm.demo.repository;

import com.dcm.demo.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer>, JpaSpecificationExecutor<Invoice> {
        Optional<Invoice> findByPayosOrder(Long payosCode);

        Optional<Invoice> findByCode(String code);

        @Query("""
                        SELECT i FROM Invoice i WHERE
                        (:code IS NULL OR i.code LIKE %:code%) AND
                        (:status IS NULL OR i.status = :status) AND
                        (:method IS NULL OR i.paymentMethod = :method) AND
                        (:fromDate IS NULL OR i.paymentDate >= :fromDate) AND
                        (:toDate IS NULL OR i.paymentDate <= :toDate)

                        """)
        Page<Invoice> findAll(Pageable pageable,
                        String code,
                        Invoice.PaymentStatus status,
                        Invoice.PaymentMethod method,
                        LocalDateTime fromDate,
                        LocalDateTime toDate);

        // Report queries
        @Query("""
                        SELECT COALESCE(SUM(i.totalAmount), 0)
                        FROM Invoice i
                        WHERE i.paymentDate BETWEEN :fromDate AND :toDate
                        AND i.status = com.dcm.demo.model.Invoice.PaymentStatus.DA_THANH_TOAN
                        """)
        java.math.BigDecimal getTotalRevenue(LocalDateTime fromDate, LocalDateTime toDate);

        @Query("""
                        SELECT CAST(i.paymentDate AS LocalDate), COALESCE(SUM(i.totalAmount), 0), COUNT(i)
                        FROM Invoice i
                        WHERE i.paymentDate BETWEEN :fromDate AND :toDate
                        AND i.status = com.dcm.demo.model.Invoice.PaymentStatus.DA_THANH_TOAN
                        GROUP BY CAST(i.paymentDate AS LocalDate)
                        ORDER BY CAST(i.paymentDate AS LocalDate)
                        """)
        List<Object[]> getRevenueByDay(LocalDateTime fromDate, LocalDateTime toDate);

        @Query("""
                        SELECT i.paymentMethod, COALESCE(SUM(i.totalAmount), 0), COUNT(i)
                        FROM Invoice i
                        WHERE i.paymentDate BETWEEN :fromDate AND :toDate
                        AND i.status = com.dcm.demo.model.Invoice.PaymentStatus.DA_THANH_TOAN
                        GROUP BY i.paymentMethod
                        """)
        List<Object[]> getRevenueByPaymentMethod(LocalDateTime fromDate, LocalDateTime toDate);

        @Query("""
                        SELECT COUNT(i)
                        FROM Invoice i
                        WHERE i.paymentDate BETWEEN :fromDate AND :toDate
                        AND i.status = :status
                        """)
        Long countByStatusAndDateRange(LocalDateTime fromDate, LocalDateTime toDate, Invoice.PaymentStatus status);
}
