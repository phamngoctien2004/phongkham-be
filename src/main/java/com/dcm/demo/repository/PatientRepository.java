package com.dcm.demo.repository;

import com.dcm.demo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer>, JpaSpecificationExecutor<Patient> {
    Optional<Patient> findByPhone(String phone);

    Optional<Patient> findByCccd(String cccd);

    Optional<Patient> findBySync(String sync);

    // Report queries - sử dụng native query để tránh validation issue
    @Query(value = """
            SELECT COUNT(*) FROM benh_nhan p
            WHERE p.ngay_dang_ky BETWEEN :fromDate AND :toDate
            """, nativeQuery = true)
    Long countNewPatients(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query(value = """
            SELECT DATE(p.ngay_dang_ky), COUNT(*)
            FROM benh_nhan p
            WHERE p.ngay_dang_ky BETWEEN :fromDate AND :toDate
            GROUP BY DATE(p.ngay_dang_ky)
            ORDER BY DATE(p.ngay_dang_ky)
            """, nativeQuery = true)
    List<Object[]> getNewPatientsByDay(@Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query(value = """
            SELECT p.gioi_tinh, COUNT(*)
            FROM benh_nhan p
            WHERE p.ngay_dang_ky BETWEEN :fromDate AND :toDate
            GROUP BY p.gioi_tinh
            """, nativeQuery = true)
    List<Object[]> getPatientsByGender(@Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query(value = """
            SELECT COUNT(DISTINCT a.id_benh_nhan)
            FROM dat_lich_kham a
            INNER JOIN benh_nhan p ON a.id_benh_nhan = p.id_benh_nhan
            WHERE a.ngay_kham BETWEEN :fromDate AND :toDate
            AND DATE(p.ngay_dang_ky) < :fromDate
            AND a.trang_thai = 'HOAN_THANH'
            """, nativeQuery = true)
    Long countReturningPatients(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query(value = """
            SELECT
                CASE
                    WHEN YEAR(:currentDate) - YEAR(p.ngay_sinh) < 18 THEN 'Dưới 18'
                    WHEN YEAR(:currentDate) - YEAR(p.ngay_sinh) BETWEEN 18 AND 30 THEN '18-30'
                    WHEN YEAR(:currentDate) - YEAR(p.ngay_sinh) BETWEEN 31 AND 50 THEN '31-50'
                    WHEN YEAR(:currentDate) - YEAR(p.ngay_sinh) BETWEEN 51 AND 65 THEN '51-65'
                    ELSE 'Trên 65'
                END AS age_group,
                COUNT(*) AS patient_count
            FROM benh_nhan p
            WHERE p.ngay_dang_ky BETWEEN :fromDate AND :toDate
            GROUP BY age_group
            ORDER BY
                CASE age_group
                    WHEN 'Dưới 18' THEN 1
                    WHEN '18-30' THEN 2
                    WHEN '31-50' THEN 3
                    WHEN '51-65' THEN 4
                    ELSE 5
                END
            """, nativeQuery = true)
    List<Object[]> getPatientsByAgeGroup(@Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate, @Param("currentDate") LocalDate currentDate);
}
