package com.dcm.demo.repository;

import com.dcm.demo.model.HealthPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
}
