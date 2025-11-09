package com.dcm.demo.repository;

import com.dcm.demo.model.HealthPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExaminationServiceRepository extends JpaRepository<HealthPlan, Integer>, JpaSpecificationExecutor<HealthPlan> {
    List<HealthPlan> findAllByIdIn(List<Integer> ids);


    @Query("""
                    SELECT hp FROM HealthPlan hp
                    JOIN FETCH  Room r on r.roomId = hp.room.roomId
                    WHERE (:keyword IS NULL OR hp.name LIKE %:keyword% OR hp.description LIKE %:keyword%)
                      AND (:priceFrom IS NULL OR hp.price >= :priceFrom)
                      AND (:priceTo IS NULL OR hp.price <= :priceTo)
                      AND (:type IS NULL OR hp.type = :type)
            """)
    Page<HealthPlan> findAll(String keyword,
                             BigDecimal priceFrom,
                             BigDecimal priceTo,
                             HealthPlan.ServiceType type,
                             Pageable pageable);

    // Report queries
    @Query("""
            SELECT hp.id, hp.name, COUNT(a), SUM(a.totalAmount), hp.price
            FROM Appointment a
            JOIN a.healthPlan hp
            WHERE a.date BETWEEN :fromDate AND :toDate
            GROUP BY hp.id, hp.name, hp.price
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> getPopularServices(LocalDate fromDate, LocalDate toDate);

    @Query("""
            SELECT dep.id, dep.name, COUNT(DISTINCT hp.id), COUNT(a), SUM(a.totalAmount)
            FROM Appointment a
            JOIN a.healthPlan hp
            JOIN a.doctor d
            JOIN d.department dep
            WHERE a.date BETWEEN :fromDate AND :toDate
            GROUP BY dep.id, dep.name
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> getServicesByDepartment(LocalDate fromDate, LocalDate toDate);
}
